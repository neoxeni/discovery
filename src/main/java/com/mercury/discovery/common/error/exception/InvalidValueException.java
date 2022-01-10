package com.mercury.discovery.common.error.exception;


import com.mercury.discovery.common.error.BusinessException;
import com.mercury.discovery.common.error.ErrorCode;

public class InvalidValueException extends BusinessException {
    public InvalidValueException(String value) {
        super(value, ErrorCode.INVALID_INPUT_VALUE);
    }

    public InvalidValueException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }
}
