package com.mercury.discovery.common.excel.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.IndexedColors;

@Builder
@Getter
@Setter
public class ExcelOptions {

    private short headerFontsize;
    private short bodyFontsize;
    private short headerBackgroundColor;
    private boolean headerFreeze;
    private String sheetName;
    private String filename;
    private boolean fixedWidth;

    public static ExcelOptions of(String fileName) {
        return ExcelOptions.builder()
                .headerFreeze(true)         // 헤더 로우 스크롤 고정 여부
                .filename(fileName)         // 파일명, default: untiled.xlsx, 파일명이 .xslx로 끝나지 않으면 마지막에 .xlsx을 붙여준다.
                .bodyFontsize((short) 12)   // body 영역의 폰트 사이즈, default: 10
                .headerFontsize((short) 10) // 헤더 영역의 폰트 사이즈, default: 10
                .headerBackgroundColor(IndexedColors.GREY_25_PERCENT.index) // 헤더영역의 bg 색상, default : IndexedColors.GREY_25_PERCENT.index
                .build();
    }
}
