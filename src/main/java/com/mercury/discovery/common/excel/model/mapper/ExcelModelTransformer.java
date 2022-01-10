package com.mercury.discovery.common.excel.model.mapper;

import com.mercury.discovery.common.error.exception.InvalidValueException;
import com.mercury.discovery.common.excel.model.mapper.validator.CellValueValidator;
import com.mercury.discovery.util.StringFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class ExcelModelTransformer {

    private Workbook workbook;
    private Class<? extends ExcelModel> modelClass;

    public ExcelModelTransformer(MultipartFile multipartFile, Class<? extends ExcelModel> modelClass) throws IOException {
        this.modelClass = modelClass;
        if (FilenameUtils.getExtension(multipartFile.getOriginalFilename()).equals("xlsx")) {
            workbook = new XSSFWorkbook(multipartFile.getInputStream());
        } else {
            workbook = new HSSFWorkbook(multipartFile.getInputStream());
        }
    }

    public ExcelModelTransformer(File file, Class<? extends ExcelModel> modelClass) throws IOException, InvalidFormatException {
        this.modelClass = modelClass;
        if (FilenameUtils.getExtension(file.getName()).equals("xlsx")) {
            workbook = new XSSFWorkbook(file);
        } else {
            workbook = new HSSFWorkbook(Files.newInputStream(file.toPath()));
        }
    }


    public List<ExcelModel> getModel() {
        return this.getModel(0, 1);
    }

    public List<ExcelModel> getModel(int sheet, int startRow) {
        Sheet worksheet = workbook.getSheetAt(sheet);
        List<ExcelModel> result = new ArrayList<>();

        Field fields[] = modelClass.getDeclaredFields();

        for (int i = startRow; i < worksheet.getPhysicalNumberOfRows(); i++) {
            try {
                ExcelModel model = modelClass.newInstance();
                Row row = worksheet.getRow(i);
                int numberOfNum = row.getLastCellNum();
                for (int c = 0; c < numberOfNum; c++) {
                    Cell cell = row.getCell(c);
                    Field currentField = this.findField(fields, c);
                    if (currentField != null) {

                        Class fieldType = currentField.getType();

                        ExcelModelCellMeta excelModelCellMeta
                                = currentField.getDeclaredAnnotation(ExcelModelCellMeta.class);

                        String name = StringUtils.hasLength(excelModelCellMeta.name()) ? excelModelCellMeta.name() : currentField.getName();

                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            if (excelModelCellMeta.required()) {
                                model.setInvalid(true);
                                model.addInvalidMessage(name + " 누락");
                            }
                        } else {
                            PropertyDescriptor pd = new PropertyDescriptor(currentField.getName(), modelClass);
                            Method setter = pd.getWriteMethod();
                            CellType cellType = cell.getCellType();

                            Object resultValue = null;

                            if (fieldType.isAssignableFrom(String.class)) {
                                if (cellType == CellType.NUMERIC) {
                                    resultValue = String.valueOf(cell.getNumericCellValue());
                                }else {
                                    resultValue = cell.getStringCellValue();
                                }

                                if(StringUtils.isEmpty(resultValue) && excelModelCellMeta.required()) {
                                    model.setInvalid(true);
                                    model.addInvalidMessage(name + " 누락");
                                }
                                setter.invoke(model, resultValue);
                            } else if (fieldType.isAssignableFrom(Integer.class)) {

                                if (cellType == CellType.NUMERIC) {
                                    double value = cell.getNumericCellValue();
                                    resultValue = (int) value;
                                    setter.invoke(model, resultValue);
                                } else {
                                    String value = cell.getStringCellValue();
                                    if (!StringFormatUtils.isNumeric(value)) {
                                        model.setInvalid(true);
                                        model.addInvalidMessage(name + " 타입 오류 => " + value);
                                    } else {
                                        setter.invoke(model, Integer.valueOf(value));
                                    }
                                }

                            } else if (fieldType.isAssignableFrom(Double.class)) {
                                if (cellType == CellType.NUMERIC) {
                                    double value = cell.getNumericCellValue();
                                    resultValue = value;
                                    setter.invoke(model, resultValue);
                                } else {
                                    String value = cell.getStringCellValue();
                                    resultValue = value;
                                    if (!StringFormatUtils.isNumeric(value)) {
                                        model.setInvalid(true);
                                        model.addInvalidMessage(name + " 타입 오류");
                                    } else {
                                        setter.invoke(model, Double.parseDouble(value));
                                    }
                                }
                            } else {
                                throw new InvalidValueException("ExcelModelCellMeta 타입 오류");
                            }

                            Class<? extends CellValueValidator> cellValueValidator[] = excelModelCellMeta.validator();
                            for (Class<? extends CellValueValidator> clazz : cellValueValidator) {
                                if (!clazz.newInstance().validate(cell)) {
                                    model.setInvalid(true);
                                    model.addInvalidMessage(name + " 형식 오류 => " + resultValue);
                                }
                            }
                        }
                    }
                }
                result.add(model);
            } catch (InstantiationException | IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                log.error("getModel", e);
            }

        }

        return result;
    }

    private Field findField(Field fields[], int cellNum) {
        Optional<Field> optional = Stream.of(fields).filter(f -> f.isAnnotationPresent(ExcelModelCellMeta.class)
                && f.getDeclaredAnnotation(ExcelModelCellMeta.class).cellNum() == cellNum).findFirst();
        return optional.isPresent() ? optional.get() : null;

    }
}
