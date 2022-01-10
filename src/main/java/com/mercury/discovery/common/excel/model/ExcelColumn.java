package com.mercury.discovery.common.excel.model;

import com.mercury.discovery.common.excel.ValueTransformer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"columnName"})
public class ExcelColumn implements Comparable<ExcelColumn> {


    @Override
    public int compareTo(ExcelColumn o) {
        if (this.order < o.getOrder()) {
            return -1;
        } else if (this.order > o.getOrder()) {
            return 1;
        }
        return 0;
    }

    public enum ColumnAlign {
        LEFT, CENTER, RIGHT
    }

    private ColumnAlign columnAlign;
    private int width;
    private String columnName;
    private String columnTitle;
    private List<ExcelColumn> children;
    private List<ExcelColumn> pivotColumns;
    private boolean locked;
    private CellStyle cellStyle;
    private int order;
    private boolean pivot;
    private String dateFormatPattern;
    private ValueTransformer<?> valueTransformer;

    private String valueExpression;
    private String name;
    private int pivotIndex;


    // 아래 두개는 추후 지원예정
    private boolean calculateSum;
    private boolean calculateGroupSum;

    public ExcelColumn add(ExcelColumn excelColumn) {

        if (this.children == null) {
            this.children = new ArrayList<>();
        }

        this.children.add(excelColumn);

        return this;
    }

    public static ExcelColumnBuilder ofb(String columnName, String columnTitle, Integer width, String align) {
        ColumnAlign columnAlign = ColumnAlign.LEFT;
        if (align != null) {
            columnAlign = ColumnAlign.valueOf(align);
        }

        if (width == null) {
            width = 200;//auto가 지원되면 자동하면 좋을듯;
        }


        return ExcelColumn.builder()
                .columnName(columnName)
                .columnTitle(columnTitle)
                .width(width)
                .columnAlign(columnAlign);
    }

    public static ExcelColumn of(String columnName, String columnTitle, Integer width, String align) {
        return ofb(columnName, columnTitle, width, align).build();
    }
}
