package com.mercury.discovery.common.excel;

import com.mercury.discovery.common.excel.model.ExcelColumn;
import com.mercury.discovery.common.excel.model.ExcelHeader;
import com.mercury.discovery.common.excel.model.ExcelOptions;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.List;


/**
 * [우아한 Excel Download](https://woowabros.github.io/experience/2020/10/08/excel-download.html?fbclid=IwAR0cLjD9k6Pfj5d04e1UO9meANzAdLNYKaEm2i0Q3R0oLrptHudV3v4YGwk)
 * [Excel Upload](https://cla9.tistory.com/m/118?fbclid=IwAR0x18DLVunw2bM9CSc0IfOOpP52dEphvQP1KAPai0SxLbK8QqsWZKdAAaU)
 */

public class ExcelUtils {
    public static ResultExcelDataHandler<?> getResultExcelDataHandler(String fileName, List<ExcelColumn> columns) {
        ExcelOptions excelOptions = ExcelOptions.builder()
                .headerFreeze(true)         // 헤더 로우 스크롤 고정 여부
                .sheetName("Sheet1")        // 시트명, default: Sheet1
                .filename(fileName)         // 파일명, default: untiled.xlsx, 파일명이 .xslx로 끝나지 않으면 마지막에 .xlsx을 붙여준다.
                .bodyFontsize((short) 12)   // body 영역의 폰트 사이즈, default: 10
                .headerFontsize((short) 10) // 헤더 영역의 폰트 사이즈, default: 10
                .headerBackgroundColor(IndexedColors.GREY_25_PERCENT.index) // 헤더영역의 bg 색상, default : IndexedColors.GREY_25_PERCENT.index
                .build();


        // ExcelHeader에 Model 추가후 생성
        ExcelHeader excelHeader = ExcelHeader.builder().excelColumns(columns).build();

        return new ResultExcelDataHandler<>(excelOptions, excelHeader);
    }

    public static ExcelColumn column(String columnName, String columnTitle) {
        return ExcelColumn.of(columnName, columnTitle, 0, "LEFT");
    }

    public static ExcelColumn column(String columnName, String columnTitle, Integer width) {
        return ExcelColumn.of(columnName, columnTitle, width, "LEFT");
    }

    public static ExcelColumn column(String columnName, String columnTitle, String align) {
        return ExcelColumn.of(columnName, columnTitle, 0, align);
    }

    public static ExcelColumn column(String columnName, String columnTitle, Integer width, String align) {
        return ExcelColumn.of(columnName, columnTitle, width, align);
    }

    public static ExcelColumn.ExcelColumnBuilder builder(String columnName, String columnTitle, Integer width, String align) {
        return ExcelColumn.ofb(columnName, columnTitle, width, align);
    }
}
