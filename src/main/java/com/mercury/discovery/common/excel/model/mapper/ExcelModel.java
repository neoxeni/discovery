package com.mercury.discovery.common.excel.model.mapper;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class ExcelModel {

    @Setter
    @Getter
    protected boolean invalid;
    @Setter
    @Getter
    protected List<String> invalidMessages;

    public ExcelModel() {
        invalidMessages = new ArrayList<>();
    }

    public void addInvalidMessage(String message) {
        invalidMessages.add(message);
    }

}
