package com.mercury.discovery.common.model.date;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateRangeParam {
    String name() default "dateRangeParam";    //request parameter name

    String delimiter() default " ~ ";           //delimiter for two date value

    String pattern() default "yyyy-MM-dd";      //parameter value pattern

    boolean nullable() default false;           //해당 값이 없는 경우 null로 리턴할지.. false 인 경우 오늘 날짜로 리턴
}