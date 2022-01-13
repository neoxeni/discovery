package com.mercury.discovery.base.group.web;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.group.model.*;
import com.mercury.discovery.base.group.service.GroupService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.excel.ExcelUtils;
import com.mercury.discovery.common.excel.ResultExcelDataHandler;
import com.mercury.discovery.common.excel.model.ExcelColumn;
import com.mercury.discovery.common.model.date.DateRange;
import com.mercury.discovery.common.web.SimpleResponseModel;
import com.mercury.discovery.utils.MessagesUtils;
import com.mercury.discovery.utils.PagesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupRestController {

    private final GroupService groupService;

    @GetMapping("/base/groups")
    public ResponseEntity<?> getGroups(AppUser appUser) {
        return ResponseEntity.ok(groupService.findGroupAll(appUser.getClientId()));
    }

    @PostMapping("/base/groups")
    public ResponseEntity<?> postGroups(AppUser appUser, @RequestBody Group group) {
        group.setCreatedAt(LocalDateTime.now());
        group.setCreatedBy(appUser.getId());
        group.setClientId(appUser.getClientId());

        if (StringUtils.isEmpty(group.getUpdEnableYn())) {
            group.setUpdEnableYn("Y");
        }

        int affected = groupService.insertGroup(group);

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/groups")
    public ResponseEntity<?> patchGroups(AppUser appUser, @RequestBody Group group) {
        group.setUpdatedAt(LocalDateTime.now());
        group.setUpdatedBy(appUser.getId());
        group.setClientId(appUser.getClientId());
        int affected = groupService.updateGroup(group);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/groups/{id}")
    public ResponseEntity<?> deleteGroups(AppUser appUser, @PathVariable Long id) {
        int affected = groupService.deleteGroup(appUser.getClientId(), id);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @DeleteMapping("/base/groups")
    public ResponseEntity<?> deleteGroups(AppUser appUser, @RequestBody Group group) {
        int affected = groupService.deleteGroup(appUser.getClientId(), group.getId());
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @GetMapping("/base/groups/mappings")
    public ResponseEntity<?> getGroupsMappings(AppUser appUser, GroupMappingRequestDto groupMappingRequestDto, Pageable pageable) {
        groupMappingRequestDto.setClientId(appUser.getClientId());
        Page<GroupMappingResponseDto> list = groupService.findGroupMappingsByGrpNo(groupMappingRequestDto, pageable);
        return ResponseEntity.ok(PagesUtils.of(list));
    }

    @GetMapping("/base/groups/mappings/excel")
    public void getGroupsMappingsExcel(AppUser appUser, GroupMappingRequestDto groupMappingRequestDto, Pageable pageable) {
        groupMappingRequestDto.setClientId(appUser.getClientId());

        List<ExcelColumn> columns = new ArrayList<>();

        columns.add(ExcelUtils.column("tooltip", "구분", 150));
        columns.add(ExcelUtils.column("dataNm", "이름", 150));
        columns.add(ExcelUtils.column("useYn", "사용여부", 100));

        Group group = groupService.findGroup(appUser.getClientId(), groupMappingRequestDto.getGrpNo());

        ResultExcelDataHandler<?> resultExcelDataHandler = ExcelUtils.getResultExcelDataHandler(group.getName() + " 그룹 구성원", columns);
        groupService.downloadExcelGroupMappingsByGrpNo(groupMappingRequestDto, pageable, resultExcelDataHandler);
        resultExcelDataHandler.download();
    }

    @PostMapping("/base/groups/mappings")
    public ResponseEntity<?> postGroupsMappings(AppUser appUser, @RequestBody List<GroupMapping> groupMappings) {
        int affected = 0;

        if (groupMappings.size() > 0) {
            LocalDateTime now = LocalDateTime.now();

            int idx = 1;
            for (GroupMapping groupMapping : groupMappings) {
                groupMapping.setCreatedAt(now);
                groupMapping.setCreatedBy(appUser.getId());
                groupMapping.setUseYn("Y");
                groupMapping.setSort(idx++);
            }

            affected = groupService.insertGroupMappings(groupMappings);

            List<GroupMappingHistory> groupMappingHistories = groupService.genGroupMappingHistories(appUser, groupMappings, "C");
            groupService.insertGroupMappingsHistory(groupMappingHistories);
        }

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/group/mappings")
    public ResponseEntity<?> patchGroupsMappings(AppUser appUser,
                                                 @RequestPart("grpNo") String grpNo,
                                                 @RequestPart("groupMappings") List<GroupMapping> groupMappings) {

        groupService.updateGroupMapping(appUser, Long.parseLong(grpNo), groupMappings);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/base/groups/mappings")
    public ResponseEntity<?> patchGroupsMappings(AppUser appUser, @RequestBody List<GroupMapping> groupMappings) {
        LocalDateTime now = LocalDateTime.now();

        List<GroupMapping> mergeMappings = new ArrayList<>();
        List<GroupMapping> deleteMappings = new ArrayList<>();

        int index = 1;
        for (GroupMapping groupMapping : groupMappings) {
            if ("N".equals(groupMapping.getUseYn())) {
                deleteMappings.add(groupMapping);
            } else {
                if (groupMapping.getId() == null) {
                    groupMapping.setCreatedAt(now);
                    groupMapping.setCreatedBy(appUser.getId());
                    groupMapping.setUseYn("Y");
                    groupMapping.setSort(index++);

                    mergeMappings.add(groupMapping);
                }
            }
        }

        int affected = groupService.mergeGroupMappings(appUser.getClientId(), mergeMappings, deleteMappings);

        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();
        groupMappingHistories.addAll(groupService.genGroupMappingHistories(appUser, mergeMappings, "C"));
        groupMappingHistories.addAll(groupService.genGroupMappingHistories(appUser, deleteMappings, "D"));
        if (groupMappingHistories.size() > 0) {
            groupService.insertGroupMappingsHistory(groupMappingHistories);
        }


        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/groups/mappings")
    public ResponseEntity<?> deleteGroupsMappings(AppUser appUser, @RequestBody List<GroupMapping> groupMappings) {

        int affected = groupService.deleteGroupMappings(appUser.getClientId(), groupMappings);

        List<GroupMappingHistory> groupMappingHistories = groupService.genGroupMappingHistories(appUser, groupMappings, "D");
        groupService.insertGroupMappingsHistory(groupMappingHistories);

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }


    @GetMapping("/base/groups/mappings/histories")
    public ResponseEntity<?> getGroupsMappingsHistories(AppUser appUser, DateRange dateRange, GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable) {
        groupMappingHistoryRequestDto.setClientId(appUser.getClientId());
        groupMappingHistoryRequestDto.setStartedAt(dateRange.getStart());
        groupMappingHistoryRequestDto.setEndedAt(dateRange.getEnd());


        Page<GroupMappingHistoryResponseDto> list = groupService.findGroupMappingsHistory(groupMappingHistoryRequestDto, pageable);
        return ResponseEntity.ok(PagesUtils.of(list));
    }

    @GetMapping("/base/groups/mappings/histories/excel")
    public void getGroupsMappingsHistoriesExcel(AppUser appUser, DateRange dateRange, GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable) {
        groupMappingHistoryRequestDto.setClientId(appUser.getClientId());
        groupMappingHistoryRequestDto.setStartedAt(dateRange.getStart());
        groupMappingHistoryRequestDto.setEndedAt(dateRange.getEnd());

        List<ExcelColumn> columns = new ArrayList<>();

        columns.add(ExcelUtils.column("name", "그룹", 200));
        columns.add(ExcelUtils.column("dataNm", "대상", 150));
        columns.add(ExcelUtils.column("regEmpNm", "변경자", 100));
        columns.add(ExcelUtils.builder("action", "행위", 100, "CENTER").valueTransformer((name, value, data) -> {
            if ("C".equals(value)) {
                return "생성";
            } else if ("D".equals(value)) {
                return "삭제";
            }

            return value;
        }).build());
        columns.add(ExcelUtils.column("createdAt", "변경일시", 165, "CENTER"));
        columns.add(ExcelUtils.column("regIp", "접속아이피", 130));

        ResultExcelDataHandler<?> resultExcelDataHandler = ExcelUtils.getResultExcelDataHandler("권한변경이력조회", columns);
        groupService.downloadExcelHistory(groupMappingHistoryRequestDto, pageable, resultExcelDataHandler);
        resultExcelDataHandler.download();
    }
}