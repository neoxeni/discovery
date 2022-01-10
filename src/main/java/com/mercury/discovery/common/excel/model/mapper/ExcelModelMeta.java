package com.mercury.discovery.common.excel.model.mapper;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelModelMeta {
    String param() default "file";
    Class<? extends ExcelModel> type();
    int sheet() default 0;
    int startRow() default 1;
}
