package com.mercury.discovery.common.error.exception;


import com.mercury.discovery.common.error.BusinessException;
import com.mercury.discovery.common.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String message) {
        super(message, ErrorCode.ENTITY_NOT_FOUND);
    }
}
