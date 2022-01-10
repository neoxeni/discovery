package com.mercury.discovery.common.excel.model.mapper;

import com.mercury.discovery.common.excel.model.mapper.validator.CellValueValidator;
import com.mercury.discovery.utils.StringFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.util.StringUtils;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelModelCellMeta {

    int cellNum();

    String name() default "";

    boolean required() default true;

    Class<? extends CellValueValidator>[] validator() default {};

    class EmailValidator implements CellValueValidator {
        @Override
        public boolean validate(Cell cell) {
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                return true;
            }
            if (cell.getCellType() != CellType.STRING) {
                return false;
            } else if (StringUtils.isEmpty(cell.getStringCellValue())) {
                return true;
            }
            return StringFormatUtils.checkEmailAddressFormat(cell.getStringCellValue());
        }
    }
}
