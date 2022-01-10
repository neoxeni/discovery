package com.mercury.discovery.common.excel.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ExcelHeader {

    private List<ExcelColumn> excelColumns;
    private Class modelClass;

}
