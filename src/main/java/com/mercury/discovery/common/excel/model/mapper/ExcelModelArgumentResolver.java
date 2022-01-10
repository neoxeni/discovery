package com.mercury.discovery.common.excel.model.mapper;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ExcelModelArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {


        if (!methodParameter.getParameterType().isAssignableFrom(List.class)
                || !methodParameter.hasParameterAnnotation(ExcelModelMeta.class)) {
            return false;
        }

        Type type = methodParameter.getGenericParameterType();
        ParameterizedType pType = (ParameterizedType) type;
        Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];


        return ExcelModel.class.isAssignableFrom(clazz);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        ExcelModelMeta excelModelMeta = methodParameter.getParameterAnnotation(ExcelModelMeta.class);

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) nativeWebRequest.getNativeRequest();
        ExcelModelTransformer excelModelTransformer = new ExcelModelTransformer(multipartRequest.getFile(excelModelMeta.param()), excelModelMeta.type());

        return excelModelTransformer.getModel(excelModelMeta.sheet(), excelModelMeta.startRow());
    }
}
