package com.mercury.discovery.common.excel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mercury.discovery.common.excel.model.ExcelColumn;
import com.mercury.discovery.common.excel.model.ExcelHeader;
import com.mercury.discovery.common.excel.model.ExcelOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class ResultExcelDataHandler<T> implements ResultHandler<T> {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ExcelHeader excelHeader;
    private SXSSFWorkbook workbook;
    private SXSSFSheet currentSheet;
    private ExcelOptions excelOptions;
    private List<ExcelColumn> flatColumns;
    private CellStyle headerCellStyle;

    private Map<String, Method> getterCached;

    private boolean closed;
    private AtomicInteger createdHeaderRownum;
    private int sheetCurrentRownum;
    private int sheetTotalRownum;
    private List<ExcelColumn> columns;

    private List<SheetColumnInfo> sheetColumnInfos;

    private ExpressionParser expressionParser;

    public static final String KEY_NAME = "result.excel.data.handler";

    public ResultExcelDataHandler(ExcelOptions excelOptions, ExcelHeader excelHeader) {

        expressionParser = new SpelExpressionParser();

        ServletRequestAttributes requestAttributes
                = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        this.sheetColumnInfos = new ArrayList<>();

        this.request = requestAttributes.getRequest();
        this.response = requestAttributes.getResponse();

        this.workbook = new SXSSFWorkbook(1000);

        this.addSheet(excelOptions, excelHeader);

        this.request.setAttribute(ResultExcelDataHandler.KEY_NAME, this);


    }

    public void addSheet() {
        this.addSheet(this.excelOptions, this.excelHeader);
    }

    public void addSheet(String sheetName) {
        this.excelOptions.setSheetName(sheetName);
        this.addSheet();
    }

    public void addSheet(ExcelOptions excelOptions) {
        this.addSheet(excelOptions, this.excelHeader);
    }

    public void addSheet(ExcelHeader excelHeader) {
        this.addSheet(this.excelOptions, excelHeader);
    }

    public void addSheet(String sheetName, ExcelHeader excelHeader) {
        this.excelOptions.setSheetName(sheetName);
        this.addSheet(this.excelOptions, excelHeader);
    }

    public void addSheet(ExcelOptions excelOptions, ExcelHeader excelHeader) {

        this.excelHeader = excelHeader;
        this.excelOptions = excelOptions;
        this.createdHeaderRownum = new AtomicInteger();
        this.columns = excelHeader.getExcelColumns();
        if (this.columns == null && excelHeader.getModelClass() != null) {
            this.columns = new ArrayList<>();
            this.getColumnsFromModel(this.excelHeader.getModelClass(), this.columns);
        }
        this.flatColumns = this.getFlatColumns(this.columns);

        String sheetName = StringUtils.hasLength(excelOptions.getSheetName()) ? excelOptions.getSheetName() : "Sheet" + this.workbook.getNumberOfSheets() + 1;

        this.createSheet(sheetName);

        boolean hasPivot = this.columns.stream().filter(e -> e.isPivot()).findFirst().isPresent();

        this.sheetColumnInfos.add(new SheetColumnInfo(this.currentSheet, this.flatColumns, Collections.emptyList(),
                hasPivot, !hasPivot));

        this.getterCached = new HashMap<>();

        this.addExcelHeader(columns, 0, 0);

        int lastLockColumnIndex = this.getLastLockColumnIndex(columns);

        if (excelOptions.isHeaderFreeze() && lastLockColumnIndex > -1) {
            currentSheet.createFreezePane(lastLockColumnIndex + 1, createdHeaderRownum.get());
        } else {
            if (excelOptions.isHeaderFreeze()) {
                currentSheet.createFreezePane(0, createdHeaderRownum.get());
            } else if (lastLockColumnIndex > -1) {
                currentSheet.createFreezePane(lastLockColumnIndex + 1, 0);
            }
        }

        this.sheetTotalRownum = 0;

        this.rowMergeHeader();
    }

    public void addRow(T data, int rownum) {
        if (rownum == 0) {
            // 신규 쿼리의 시작
            this.sheetCurrentRownum = this.sheetTotalRownum;

        }

        if (!this.currentSheetColumnInfo().isCompletePivotHeader()) {
            this.createPivotHeader(data, this.columns);
        }

        this.addExcelBody(this.flatColumns, data, createdHeaderRownum.get() + this.sheetCurrentRownum, 0);

        this.sheetTotalRownum++;
        this.sheetCurrentRownum++;
    }

    @Override
    public void handleResult(ResultContext<? extends T> resultContext) {

        T data = resultContext.getResultObject();
        int rownum = resultContext.getResultCount() - 1;

        this.addRow(data, rownum);
    }

    private void createPivotHeader(Object data, List<ExcelColumn> columns) {
        List<ExcelColumn> pivotColumnsAll = new ArrayList<>();
        columns.stream().filter(e -> e.isPivot()).forEach(column -> {
            List<ExcelColumn> pivotColumns = new ArrayList<>();
            String columnName = column.getColumnName();
            Field field = null;
            try {
                field = data.getClass().getDeclaredField(columnName);
            } catch (NoSuchFieldException e) {
                log.error("createPivotHeader", e);
            }

            if (field != null) {
                if (field.getType().isAssignableFrom(List.class)) {
                    ParameterizedType type = (ParameterizedType) field.getGenericType();
                    Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
                    List list = (List) this.invokeGetter(data, columnName);
                    if (!CollectionUtils.isEmpty(list)) {
                        for (Object o : list) {
                            this.getColumnsFromModel(clazz, pivotColumns, o);
                        }

                        this.currentSheetColumnInfo().setCompletePivotHeader(true);
                    }
                }
            }

            // 중복컬럼 제거
            pivotColumns = new ArrayList<>(new LinkedHashSet<>(pivotColumns));

            column.setPivotColumns(pivotColumns);
            pivotColumnsAll.addAll(pivotColumns);
        });

        if (pivotColumnsAll.size() == 0) {
            return;
        }

        this.currentSheetColumnInfo().setPivotColumns(pivotColumnsAll);

        pivotColumnsAll.forEach(this::setStyle);

        this.addExcelHeader(pivotColumnsAll, 0, currentSheet.getRow(0).getLastCellNum());


    }

    private void createSheet(String sheetName) {

        try {
            this.currentSheet = workbook.createSheet(sheetName);
        } catch (IllegalArgumentException illegalArgumentException) {
            String message = illegalArgumentException.getMessage();
            if (message.contains("already contains")) {
                this.createSheet(sheetName + "-2");
            }
        }

    }

    private void addExcelBody(List<ExcelColumn> columns, Object data, int rownum, int cellnum) {
        this.addExcelBody(columns, data, rownum, cellnum, false);
    }

    private void addExcelBody(List<ExcelColumn> columns, Object data, int rownum, int cellnum, boolean isPivot) {
        SXSSFCell cell;
        SXSSFRow row = currentSheet.getRow(rownum);
        if (row == null) {
            row = currentSheet.createRow(rownum);
        }

        for (ExcelColumn column : columns) {
            String name = column.getColumnName();
            Object value = this.evalExpressionColumnValue(data, column);

            if (value != null && column.isPivot() && value instanceof List) {

                Map pivotMap = new HashMap();
                List<Object> list = (List) value;

                List<ExcelColumn> uniquePivotColumns = new ArrayList<>();

                for (int i = 0; i < column.getPivotColumns().size(); i++) {
                    ExcelColumn pivotColumn = column.getPivotColumns().get(i);
                    Object target = list.get(i);
                    Object columnValue = this.evalExpressionColumnValue(target, pivotColumn.getValueExpression(), pivotColumn.getName());
                    ValueTransformer valueTransformer = pivotColumn.getValueTransformer();
                    if (valueTransformer != null) {
                        columnValue = valueTransformer.transform(pivotColumn.getColumnName(), columnValue, target);
                    }
                    String columnName = pivotColumn.getColumnName();
                    if(pivotMap.containsKey(columnName)) {
                        String previousValue = String.valueOf(pivotMap.get(columnName));
                        pivotMap.put(columnName, previousValue + "\n" + columnValue);
                    }else {
                        uniquePivotColumns.add(pivotColumn);
                        pivotMap.put(columnName, columnValue);
                    }
                }

                this.addExcelBody(uniquePivotColumns, pivotMap, rownum, cellnum, true);

                return;
            }

            cell = this.createCell(row, cellnum, column);


            if(!isPivot) {
                ValueTransformer valueTransformer = column.getValueTransformer();

                if (valueTransformer == null || valueTransformer.getClass() == DefaultValueTransformer.class) {
                    if (value instanceof LocalDateTime &&
                            StringUtils.hasLength(column.getDateFormatPattern())) {
                        value = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(column.getDateFormatPattern()));
                    }
                } else {
                    value = valueTransformer.transform(name, value, data);
                }
            }

            String stringValue = String.valueOf(value);

            boolean isNumeric = value != null && Number.class.isAssignableFrom(value.getClass());

            if (value != null) {
                if (Number.class.isAssignableFrom(value.getClass())) {
                    cell.setCellValue(Double.parseDouble(stringValue));
                } else {
                    cell.setCellValue(stringValue);
                }
            } else {
                if (isNumeric) {
                    cell.setCellValue(0);
                } else {
                    cell.setCellValue("");
                }
            }

            cellnum++;
        }

    }

    private SXSSFCell createCell(SXSSFRow row, int index, ExcelColumn column) {
        SXSSFCell cell = row.createCell(index);
        CellStyle cellStyle = column.getCellStyle();
        cell.setCellStyle(cellStyle);
        return cell;
    }

    private List<ExcelColumn> getFlatColumns(List<ExcelColumn> columns) {
        List<ExcelColumn> flatColumns = new ArrayList<>();

        for (ExcelColumn column : columns) {
            List<ExcelColumn> children = column.getChildren();
            if (children != null && children.size() > 0) {
                List<ExcelColumn> _flatColumns = this.getFlatColumns(children);
                for (ExcelColumn _column : _flatColumns) {
                    this.setStyle(_column);
                    flatColumns.add(_column);
                }
            } else {
                this.setStyle(column);
                flatColumns.add(column);
            }
        }

        return flatColumns;
    }

    private void setStyle(ExcelColumn column) {

        CellStyle cellStyle = workbook.createCellStyle();

        short bodyFontsize = excelOptions.getBodyFontsize();
        if (bodyFontsize <= 0) {
            bodyFontsize = 10;
        }

        Font font = workbook.createFont();
        font.setFontHeightInPoints(bodyFontsize);

        HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
        if (column.getColumnAlign() == ExcelColumn.ColumnAlign.CENTER) {
            horizontalAlignment = HorizontalAlignment.CENTER;
        } else if (column.getColumnAlign() == ExcelColumn.ColumnAlign.RIGHT) {
            horizontalAlignment = HorizontalAlignment.RIGHT;
        }

        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        cellStyle.setFont(font);

        column.setCellStyle(cellStyle);

    }

    private void rowMergeHeader() {
        SXSSFRow firstRow = currentSheet.getRow(0);

        int lastRownum = currentSheet.getLastRowNum();
        int lastCellnum = firstRow.getLastCellNum();

        for (int i = lastRownum; i > 0; i--) {
            this.rowMergeHeader(i, lastCellnum);
        }
    }

    private void rowMergeHeader(int rownum, int lastCellnum) {
        SXSSFRow row = currentSheet.getRow(rownum);

        for (int i = 0; i < lastCellnum; i++) {
            SXSSFCell cell = row.getCell(i);
            if (cell == null) {
                CellRangeAddress cellAddresses = new CellRangeAddress(rownum - 1, rownum, i, i);
                currentSheet.addMergedRegion(cellAddresses);
                this.setRegionBorder(cellAddresses);
            }
        }
    }

    private void addExcelHeader(List<ExcelColumn> columns, int rowNum, int cellNum) {

        SXSSFRow row;
        SXSSFCell cell;

        row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
            createdHeaderRownum.incrementAndGet();
        }

        int emptyCellLen = 0;
        int continueLen = 0;
        Set<String> columnNameSet = new HashSet<>();
        for (int i = 0, len = columns.size(); i < len; i++) {
            ExcelColumn column = columns.get(i);
            if (column.isPivot()) {
                continueLen++;
                continue;
            }

            if(!columnNameSet.add(column.getColumnName())) {
                continueLen++;
                continue;
            }

            int createCellIndex = i + emptyCellLen + cellNum - continueLen;
            String title = column.getColumnTitle();

            if (!StringUtils.hasLength(title)) {
                title = column.getColumnName();
            }

            cell = this.createHeaderCell(row, createCellIndex);
            cell.setCellValue(title);

            List<ExcelColumn> children = column.getChildren();
            if (children != null && children.size() > 0) {
                int mergeCellLen = this.getMergeCellLength(children);

                for (int j = 1; j <= mergeCellLen; j++) {
                    cell = this.createHeaderCell(row, createCellIndex + j);
                    cell.setCellValue(".");
                    emptyCellLen++;
                }

                if (mergeCellLen > 0) {
                    CellRangeAddress cellAddresses = new CellRangeAddress(rowNum, cellNum, createCellIndex, createCellIndex + mergeCellLen);
                    currentSheet.addMergedRegion(cellAddresses);
                    this.setRegionBorder(cellAddresses);
                }

                this.addExcelHeader(children, rowNum + 1, createCellIndex);
            }
        }
    }

    private int getLastLockColumnIndex(List<ExcelColumn> columns) {
        int lastLockColumnIndex = -1;
        for (int i = 0, len = columns.size(); i < len; i++) {
            ExcelColumn column = columns.get(i);

            if (column.isLocked()) {

                List<ExcelColumn> children = column.getChildren();

                if (children != null && children.size() > 0) {
                    lastLockColumnIndex += children.size();
                } else {
                    lastLockColumnIndex++;
                }


            }
        }

        return lastLockColumnIndex;
    }

    private void setRegionBorder(CellRangeAddress cellAddresses) {
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, currentSheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, currentSheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, currentSheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, currentSheet);
    }

    private int getMergeCellLength(List<ExcelColumn> columns) {
        int childrenLen = (columns == null || columns.size() == 0) ? 0 : columns.size() - 1;
        for (int i = 0; i < childrenLen; i++) {
            ExcelColumn column = columns.get(i);
            childrenLen += this.getMergeCellLength(column.getChildren());
        }
        return childrenLen;
    }


    private SXSSFCell createHeaderCell(SXSSFRow row, int index) {

        SXSSFCell cell = row.createCell(index);

        if (headerCellStyle == null) {
            headerCellStyle = workbook.createCellStyle();
            short headerBackgroundColor = excelOptions.getHeaderBackgroundColor();
            short headerFontsize = excelOptions.getHeaderFontsize();

            if (headerBackgroundColor <= 0) {
                headerBackgroundColor = IndexedColors.GREY_25_PERCENT.index;
            }

            if (headerFontsize <= 0) {
                headerFontsize = 10;
            }

            headerCellStyle.setFillForegroundColor(headerBackgroundColor);
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);

            Font font = workbook.createFont();
            font.setFontHeightInPoints(headerFontsize);
            headerCellStyle.setFont(font);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        }

        cell.setCellStyle(headerCellStyle);

        return cell;
    }

    private void setColumnWidth() {


        this.sheetColumnInfos.forEach(sheetColumnInfo -> {
            List<ExcelColumn> columnsAll = sheetColumnInfo.getFlatColumns();
            columnsAll.addAll(sheetColumnInfo.getPivotColumns());

            Set<ExcelColumn> filtered = new LinkedHashSet<>(columnsAll.stream().filter(e -> !e.isPivot()).collect(Collectors.toList()));


            int index = 0;
            for (ExcelColumn excelColumn : filtered) {
                // TODO 원인 찾기전까지 SXSSFSheet 에서는 autoSizeColumn에서 에러가 발생하여 무조건 width세팅하도록 함.
                /*if (excelOptions.isFixedWidth()) {
                    this.fixColumnWidth(excelColumn, i);
                } else {
                    try {
                        sheet.autoSizeColumn(i, false);
                    }catch (Exception e) {
                        log.error("Exception: {}", e);
                    }

                }*/

                this.fixColumnWidth(sheetColumnInfo.getSheet(), excelColumn, index++);
            }
        });

    }

    private void fixColumnWidth(Sheet sheet, ExcelColumn column, int index) {
        int pixcel = column.getWidth();

        if (pixcel <= 0) {
            return;
        }

        sheet.setColumnWidth(index, Math.round(pixcel / Units.DEFAULT_CHARACTER_WIDTH * 256f));
    }

    private void getColumnsFromModel(Class modelClass, List<ExcelColumn> columns) {
        this.getColumnsFromModel(modelClass, columns, null);
    }

    private void getColumnsFromModel(Class modelClass, List<ExcelColumn> columns, Object data) {

        Field fields[] = modelClass.getDeclaredFields();
        Map<String, ExcelColumn> groups = null;

        for (Field field : fields) {
            Class type = field.getType();
            String name = field.getName();
            String columnName = name;
            String title = name;
            String titleOf;
            String nameOf;
            String valueExpression;
            int width = 100;
            boolean locked = false;
            boolean pivot = false;
            int order = 0;
            ExcelColumn.ColumnAlign columnAlign = ExcelColumn.ColumnAlign.LEFT;
            String group = "";
            ValueTransformer valueTransformer = null;

            if (field.isAnnotationPresent(ExcelCell.class)) {
                ExcelCell excelCell = field.getDeclaredAnnotation(ExcelCell.class);
                if (!excelCell.visible()) {
                    continue;
                }

                valueExpression = excelCell.value();
                title = excelCell.title();
                width = excelCell.width();
                locked = excelCell.locked();
                columnAlign = excelCell.align();
                order = excelCell.order();
                group = excelCell.group();
                pivot = excelCell.pivot();
                titleOf = excelCell.titleOf();
                nameOf = excelCell.nameOf();

                if (StringUtils.hasLength(titleOf) && data != null) {
                    try {
                        title = (String) this.evalExpression(data, titleOf);
                    } catch (Exception e) {
                        log.error("expression error", e);
                    }
                }

                if (StringUtils.hasLength(nameOf) && data != null) {
                    Object value = this.invokeGetter(data, nameOf);
                    if (value != null) {
                        columnName = String.valueOf(value);
                    }
                }

                try {
                    valueTransformer = excelCell.valueTransformer().newInstance();
                } catch (Exception e) {
                    log.error("Exception: {}", name, e);
                }
            } else {
                continue;
            }

            ExcelColumn groupColumn = null;
            if (StringUtils.hasLength(group)) {
                if (groups == null) {
                    groups = new HashMap<>();
                }

                groupColumn = groups.get(group);
                if (groupColumn == null) {
                    groupColumn = ExcelColumn.builder()
                            .columnTitle(group).build();
                    groups.put(group, groupColumn);
                    columns.add(groupColumn);
                }
            }

            ExcelColumn column = ExcelColumn.builder()
                    .columnTitle(title)
                    .columnName(columnName)
                    .order(order)
                    .width(width)
                    .columnAlign(columnAlign)
                    .locked(locked)
                    .valueTransformer(valueTransformer)
                    .pivot(pivot)
                    .name(name)
                    .valueExpression(valueExpression)
                    .build();

            if (field.isAnnotationPresent(JsonFormat.class)) {
                JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
                if (type.isAssignableFrom(LocalDateTime.class)) {
                    column.setDateFormatPattern(jsonFormat.pattern());
                }
            }

            if (groupColumn != null) {
                groupColumn.add(column);
            } else {
                columns.add(column);
            }
        }

        Collections.sort(columns);
    }

    public void download() {

        try {
            this.setColumnWidth();

            String filename = excelOptions.getFilename();

            if (!StringUtils.hasLength(filename)) {
                filename = "untitled.xlsx";
            }

            if (!filename.endsWith(".xlsx")) {
                filename += ".xlsx";
            }

            String disposition = this.getDisposition(filename);

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, disposition);
            workbook.write(response.getOutputStream());

        } catch (Exception e) {
            log.error("Exception", e);

            response.setContentType("application/vnd.ms-excel; charset=utf-8");

            try {
                OutputStream out = response.getOutputStream();
                StreamUtils.copy("excel Download Fail.".getBytes(StandardCharsets.UTF_8), out);
            } catch (Exception ex) {
                log.error("Exception", ex);
            }

        } finally {
            this.close();
        }

    }

    public void close() {
        if (workbook != null) {
            workbook.dispose();
            closed = true;
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("Exception", e);
            }
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    private Object invokeGetter(Object object, String propertyName) {
        try {
            Method getter = getterCached.get(object.getClass().getSimpleName() + "." + propertyName);
            if (getter == null) {
                PropertyDescriptor pd = new PropertyDescriptor(propertyName, object.getClass());
                getter = pd.getReadMethod();
                getterCached.put(object.getClass().getSimpleName() + "." + propertyName, getter);
            }
            return getter.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
            log.error("Exception: invokeGetter {}", propertyName, e);
        }

        return null;
    }

    private Object evalExpressionColumnValue(Object data, ExcelColumn excelColumn) {
        return this.evalExpressionColumnValue(data, excelColumn.getValueExpression(), excelColumn.getColumnName());
    }

    private Object evalExpressionColumnValue(Object data, String expressionString, String name) {

        if (StringUtils.hasLength(expressionString)) {
            return evalExpression(data, expressionString);
        }

        if (data instanceof Map) {
            return ((Map) data).get(name);
        } else {
            return this.invokeGetter(data, name);
        }
    }

    private Object evalExpression(Object data, String expressionString) {
        Expression expression = expressionParser.parseExpression(expressionString);
        EvaluationContext context = new StandardEvaluationContext(data);
        return expression.getValue(context);
    }

    private SheetColumnInfo currentSheetColumnInfo() {
        return this.sheetColumnInfos.get(this.sheetColumnInfos.size() - 1);
    }

    private String getDisposition(String filename) {
        return "attachment;filename=" + getEncodedFileName(request, filename);
    }

    public static String getEncodedFileName(HttpServletRequest req, String fileName) {
        String disposition = "";
        String userAgent = req.getHeader("User-Agent");

        try {
            if (!userAgent.contains("MSIE") && !userAgent.contains("Trident")) {
                if (userAgent.contains("Chrome")) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fileName.length(); ++i) {
                        char c = fileName.charAt(i);
                        if (c > '~') {
                            sb.append(URLEncoder.encode("" + c, "UTF-8"));
                        } else {
                            sb.append(c);
                        }
                    }

                    disposition = disposition + sb;
                } else {
                    disposition = disposition + "\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), "8859_1") + "\"";
                }
            } else {
                disposition = disposition + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            }
        } catch (UnsupportedEncodingException var7) {
            disposition = disposition + "\"" + fileName + "\"";
        }

        return disposition;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class SheetColumnInfo {
        private Sheet sheet;
        private List<ExcelColumn> flatColumns;
        private List<ExcelColumn> pivotColumns;
        boolean hasPivot;
        boolean completePivotHeader;
    }
}

