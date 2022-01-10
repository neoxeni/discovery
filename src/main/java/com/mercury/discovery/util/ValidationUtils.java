package com.mercury.discovery.util;

import com.mercury.discovery.common.error.exception.BadParameterException;

import java.util.List;

public class ValidationUtils {
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new BadParameterException(message);
        return obj;
    }

    public static <T> List<T> requireNonEmpty(List<T> obj, String message) {
        if(obj == null || obj.size() == 0){
            throw new BadParameterException(message);
        }

        return obj;
    }
}
