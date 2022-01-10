package com.mercury.discovery.common.excel;



import com.mercury.discovery.common.excel.model.ExcelColumn;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelCell {
    String value() default "";
    String title() default "";
    String titleOf() default "";
    String nameOf() default "";
    int width() default 100;
    boolean locked() default false;
    ExcelColumn.ColumnAlign align() default ExcelColumn.ColumnAlign.LEFT;
    boolean visible() default true;
    int order() default -1;
    String group() default "";
    Class<? extends ValueTransformer> valueTransformer() default DefaultValueTransformer.class;

    boolean pivot() default false;

}