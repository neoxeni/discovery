package com.mercury.discovery.common.error.exception;


import com.mercury.discovery.common.error.BusinessException;
import com.mercury.discovery.common.error.ErrorCode;

public class BadParameterException extends BusinessException {
    public BadParameterException(String message) {
        super(message, ErrorCode.INVALID_INPUT_VALUE);
    }
}
