package com.mercury.discovery.base.group.web;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.group.model.*;
import com.mercury.discovery.base.group.service.GroupService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.excel.ExcelUtils;
import com.mercury.discovery.common.excel.ResultExcelDataHandler;
import com.mercury.discovery.common.excel.model.ExcelColumn;
import com.mercury.discovery.common.model.date.DateRange;
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
    public ResponseEntity<?> getGroups(AppUser appUser, @RequestParam(required = false) Integer cateId) {
        return ResponseEntity.ok(groupService.findGroupAll(appUser.getCmpnyNo(), cateId));
    }

    @PostMapping("/base/groups")
    public ResponseEntity<?> postGroups(AppUser appUser, @RequestBody Group group) {
        group.setRegDt(LocalDateTime.now());
        group.setRegUserNo(appUser.getEmpNo());
        group.setCmpnyNo(appUser.getCmpnyNo());

        if(StringUtils.isEmpty(group.getUpdEnableYn())) {
            group.setUpdEnableYn("Y");
        }
        group.setCallcenterYn("N");

        int affected = groupService.insertGroup(group);

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.insert")));
    }

    @PatchMapping("/base/groups")
    public ResponseEntity<?> patchGroups(AppUser appUser, @RequestBody Group group) {
        group.setUpdDt(LocalDateTime.now());
        group.setUpdUserNo(appUser.getEmpNo());
        group.setCmpnyNo(appUser.getCmpnyNo());
        int affected = groupService.updateGroup(group);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @DeleteMapping("/base/groups/{grpNo}")
    public ResponseEntity<?> deleteGroups(AppUser appUser, @PathVariable Integer grpNo) {
        int affected = groupService.deleteGroup(appUser.getCmpnyNo(), grpNo);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @DeleteMapping("/base/groups")
    public ResponseEntity<?> deleteGroups(AppUser appUser, @RequestBody Group group) {
        int affected = groupService.deleteGroup(appUser.getCmpnyNo(), group.getGrpNo());
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @GetMapping("/base/groups/mappings")
    public ResponseEntity<?> getGroupsMappings(AppUser appUser, GroupMappingRequestDto groupMappingRequestDto, Pageable pageable) {
        groupMappingRequestDto.setCmpnyNo(appUser.getCmpnyNo());
        Page<GroupMappingResponseDto> list = groupService.findGroupMappingsByGrpNo(groupMappingRequestDto, pageable);
        return ResponseEntity.ok(PagesUtils.of(list));
    }

    @GetMapping("/base/groups/mappings/excel")
    public void getGroupsMappingsExcel(AppUser appUser, GroupMappingRequestDto groupMappingRequestDto, Pageable pageable) {
        groupMappingRequestDto.setCmpnyNo(appUser.getCmpnyNo());

        List<ExcelColumn> columns = new ArrayList<>();

        columns.add(ExcelUtils.column("tooltip", "구분", 150));
        columns.add(ExcelUtils.column("dataNm", "이름", 150));
        columns.add(ExcelUtils.column("useYn", "사용여부", 100));

        Group group = groupService.findGroup(appUser.getCmpnyNo(), groupMappingRequestDto.getGrpNo());

        ResultExcelDataHandler<?> resultExcelDataHandler = ExcelUtils.getResultExcelDataHandler(group.getGrpNm() + " 그룹 구성원", columns);
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
                groupMapping.setRegDt(now);
                groupMapping.setRegEmpNo(appUser.getEmpNo());
                groupMapping.setUseYn("Y");
                groupMapping.setSortNo(idx++);
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

        groupService.updateGroupMapping(appUser, Integer.parseInt(grpNo), groupMappings);
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
                if (groupMapping.getMapNo() == null) {
                    groupMapping.setRegDt(now);
                    groupMapping.setRegEmpNo(appUser.getEmpNo());
                    groupMapping.setUseYn("Y");
                    groupMapping.setSortNo(index++);

                    mergeMappings.add(groupMapping);
                }
            }
        }

        int affected = groupService.mergeGroupMappings(appUser.getCmpnyNo(), mergeMappings, deleteMappings);

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

        int affected = groupService.deleteGroupMappings(appUser.getCmpnyNo(), groupMappings);

        List<GroupMappingHistory> groupMappingHistories = groupService.genGroupMappingHistories(appUser, groupMappings, "D");
        groupService.insertGroupMappingsHistory(groupMappingHistories);

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @DeleteMapping("/base/appGroup/mapping")
    public ResponseEntity<?> deleteAppGroupsMappings(AppUser appUser, @RequestBody List<AppGroupMapping> groupMappings) {

        int affected = groupService.deleteAppGroupMappings(appUser.getCmpnyNo(), groupMappings);

        List<GroupMappingHistory> groupMappingHistories = groupService.genAppGroupMappingHistories(appUser, groupMappings, "D");
        groupService.insertGroupMappingsHistory(groupMappingHistories);

        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.delete")));
    }

    @GetMapping("/base/groups/mappings/histories")
    public ResponseEntity<?> getGroupsMappingsHistories(AppUser appUser, DateRange dateRange, GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable) {
        groupMappingHistoryRequestDto.setCmpnyNo(appUser.getCmpnyNo());
        groupMappingHistoryRequestDto.setStartedAt(dateRange.getStart());
        groupMappingHistoryRequestDto.setEndedAt(dateRange.getEnd());


        Page<GroupMappingHistoryResponseDto> list = groupService.findGroupMappingsHistory(groupMappingHistoryRequestDto, pageable);
        return ResponseEntity.ok(PagesUtils.of(list));
    }

    @GetMapping("/base/groups/mappings/histories/excel")
    public void getGroupsMappingsHistoriesExcel(AppUser appUser, DateRange dateRange, GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable) {
        groupMappingHistoryRequestDto.setCmpnyNo(appUser.getCmpnyNo());
        groupMappingHistoryRequestDto.setStartedAt(dateRange.getStart());
        groupMappingHistoryRequestDto.setEndedAt(dateRange.getEnd());

        List<ExcelColumn> columns = new ArrayList<>();

        columns.add(ExcelUtils.column("grpNm", "그룹", 200));
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
        columns.add(ExcelUtils.column("regDt", "변경일시", 165, "CENTER"));
        columns.add(ExcelUtils.column("regIp", "접속아이피", 130));

        ResultExcelDataHandler<?> resultExcelDataHandler = ExcelUtils.getResultExcelDataHandler("권한변경이력조회", columns);
        groupService.downloadExcelHistory(groupMappingHistoryRequestDto, pageable, resultExcelDataHandler);
        resultExcelDataHandler.download();
    }


    @GetMapping("/base/appGroup")
    public ResponseEntity<?> getAppGroup(AppUser appUser,
                                          @RequestParam Integer cateId) {

        List<AppGroup> appGroups = groupService.selectAppGroup(appUser.getCmpnyNo(), cateId);
        return ResponseEntity.ok(appGroups);
    }

    @PostMapping("/base/appGroup")
    public ResponseEntity<?> postAppGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.insertAppGroup(appGroup, appUser);
        return ResponseEntity.ok(appGroup);
    }

    @PatchMapping("/base/appGroup")
    public ResponseEntity<?> patchAppGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.updateAppGroup(appGroup, appUser);
        return ResponseEntity.ok(appGroup);
    }

    @DeleteMapping("/base/appGroup")
    public ResponseEntity<?> deleteAppGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.deleteAppGroup(appGroup, appUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/base/appGroup/mapping")
    public ResponseEntity<?> getAppGroupMapping(AppUser appUser, @RequestParam Integer appGrpNo) {
        List<AppGroupMapping> list = groupService.selectAppGroupMapping(appUser.getCmpnyNo(), appGrpNo);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/base/appGroup/mapping")
    public ResponseEntity<?> postAppGroupMapping(AppUser appUser, @RequestBody AppGroupMapping appGroupMapping) {
        groupService.insertAppGroupMapping(appUser, appGroupMapping);
        return ResponseEntity.ok(appGroupMapping);
    }

    @PatchMapping("/base/appGroup/mapping")
    public ResponseEntity<?> patchAppGroupMapping(AppUser appUser,
                                                  @RequestPart("appGrpNo") String appGrpNo,
                                                  @RequestPart("appGroupMappings") List<AppGroupMapping> appGroupMappings) {
        groupService.updateAppGroupMapping(appUser, Integer.parseInt(appGrpNo), appGroupMappings);
        return ResponseEntity.ok().build();
    }



    /*
    *  메뉴 권한 그룹 시작
    * */

    /*@GetMapping("/base/menuGroups")
    public ResponseEntity<?> getMenuGroups(AppUser appUser,
                                          @RequestParam Integer cateId) {

        List<AppGroup> appGroups = groupService.selectAppGroup(appUser.getCmpnyNo(), cateId);
        return ResponseEntity.ok(appGroups);
    }

    @PostMapping("/base/menuGroup")
    public ResponseEntity<?> postMenuGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.insertAppGroup(appGroup, appUser);
        return ResponseEntity.ok(appGroup);
    }

    @PatchMapping("/base/menuGroup")
    public ResponseEntity<?> patchMenuGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.updateAppGroup(appGroup, appUser);
        return ResponseEntity.ok(appGroup);
    }

    @DeleteMapping("/base/menuGroup")
    public ResponseEntity<?> deleteMenuGroup(AppUser appUser, @RequestBody AppGroup appGroup) {
        groupService.deleteAppGroup(appGroup, appUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/base/menuGroup/mapping")
    public ResponseEntity<?> getMenuGroupMapping(AppUser appUser, @RequestParam Integer appGrpNo) {
        List<AppGroupMapping> list = groupService.selectAppGroupMapping(appUser.getCmpnyNo(), appGrpNo);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/base/menuGroup/mapping")
    public ResponseEntity<?> postMenuGroupMapping(AppUser appUser, @RequestBody AppGroupMapping appGroupMapping) {
        groupService.insertAppGroupMapping(appUser, appGroupMapping);
        return ResponseEntity.ok(appGroupMapping);
    }

    @PatchMapping("/base/menuGroup/mapping")
    public ResponseEntity<?> patchMenuGroupMapping(AppUser appUser,
                                                  @RequestPart("appGrpNo") String appGrpNo,
                                                  @RequestPart("appGroupMappings") List<AppGroupMapping> appGroupMappings) {
        groupService.updateAppGroupMapping(appUser, Integer.parseInt(appGrpNo), appGroupMappings);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/base/menuGroup/mapping")
    public ResponseEntity<?> deleteMenuGroupMapping(@RequestParam Integer appGrpNo) {
        groupService.deleteAppGroupMapping(appGrpNo);
        return ResponseEntity.ok(appGrpNo);
    }*/
}