package com.mercury.discovery.common.model.date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Component
public class DateRangeMethodArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(DateRange.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        //@DateRangeParam 이 선언되지 않았을 경우는 기본값으로 처리
        String parameterName = parameter.getParameterName();
        String pattern = "yyyy-MM-dd";
        String delimiter = " ~ ";
        boolean includeTime = false;
        boolean nullable = false;

        DateRangeParam dateRangeParam = parameter.getParameterAnnotation(DateRangeParam.class);
        if (dateRangeParam != null) {
            parameterName = dateRangeParam.name();
            pattern = dateRangeParam.pattern();
            delimiter = dateRangeParam.delimiter();
            includeTime = pattern.contains("HH");
            nullable = dateRangeParam.nullable();
        }

        String value = webRequest.getParameter(parameterName);
        if (value == null || "".equals(value)) {
            return nullable ? null : new DateRange();
        }

        String startStr;
        String endStr;
        String[] dates = StringUtils.split(value, delimiter);
        if (dates.length == 2) {
            startStr = dates[0];
            endStr = dates[1];
        } else if (dates.length == 1) {
            startStr = dates[0];
            endStr = dates[0];
        } else {
            return nullable ? null : new DateRange();
        }

        LocalDateTime start;
        LocalDateTime end;
        if (includeTime) {
            start = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern(pattern));
            end = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern(pattern));
        } else {
            start = LocalDateTime.parse(startStr + "000000", DateTimeFormatter.ofPattern(pattern + "HHmmss"));
            end = LocalDateTime.parse(endStr + "235959", DateTimeFormatter.ofPattern(pattern + "HHmmss"));
        }

        return new DateRange(start, end);
    }
}
