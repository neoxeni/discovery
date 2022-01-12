package com.mercury.discovery.common.log.security.web;


import com.github.pagehelper.Page;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.excel.ExcelUtils;
import com.mercury.discovery.common.excel.ResultExcelDataHandler;
import com.mercury.discovery.common.excel.model.ExcelColumn;
import com.mercury.discovery.common.log.security.model.ActionLogRequestDto;
import com.mercury.discovery.common.log.security.model.ActionLogResponseDto;
import com.mercury.discovery.common.log.service.ActionLogService;
import com.mercury.discovery.utils.PagesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionLogRestController {
    private final ActionLogService actionLogService;

    @GetMapping("/base/logs/actions")
    public ResponseEntity<?> getActionLogs(AppUser appUser, ActionLogRequestDto actionLogRequestDto, Pageable pageable) {
        actionLogRequestDto.setClientId(appUser.getClientId());
        Page<ActionLogResponseDto> list = actionLogService.findAll(actionLogRequestDto, pageable);
        return ResponseEntity.ok(PagesUtils.of(list));
    }

    @GetMapping("/base/logs/actions/excel")
    public void getTenanciesExcel(AppUser appUser, ActionLogRequestDto actionLogRequestDto, Pageable pageable) {
        actionLogRequestDto.setClientId(appUser.getClientId());

        List<ExcelColumn> columns = new ArrayList<>();
        columns.add(ExcelUtils.column("createdAt", "등록일시", 165, "CENTER"));
        columns.add(ExcelUtils.column("username", "사용자 아이디", 200));
        columns.add(ExcelUtils.column("name", "사용자", 100));
        columns.add(ExcelUtils.column("language", "국가", 70));
        columns.add(ExcelUtils.column("menu", "메뉴", 160));
        columns.add(ExcelUtils.column("subMenu", "서브메뉴", 120));
        columns.add(ExcelUtils.column("action", "행위", 140));
        columns.add(ExcelUtils.column("actionUrl", "경로", 300));
        columns.add(ExcelUtils.column("inputVal", "입력값", 300));

        ResultExcelDataHandler<?> resultExcelDataHandler = ExcelUtils.getResultExcelDataHandler("관리자로그조회", columns);
        actionLogService.downloadExcel(actionLogRequestDto, pageable, resultExcelDataHandler);
        resultExcelDataHandler.download();
    }
}
