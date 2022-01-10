package com.mercury.discovery.common.excel.model.mapper.validator;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellValueValidator {
    boolean validate(Cell cell);
}
