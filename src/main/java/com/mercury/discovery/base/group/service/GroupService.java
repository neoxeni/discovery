package com.mercury.discovery.base.group.service;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.group.model.*;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.error.BusinessException;
import com.mercury.discovery.common.error.ErrorCode;
import com.mercury.discovery.utils.HttpUtils;
import com.mercury.discovery.utils.PagesUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public List<Group> findGroupAll(int clientId) {
        return groupRepository.findGroupAll(clientId, "Y", null);
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupAll(int clientId, Integer cateId) {
        return groupRepository.findGroupAll(clientId, "Y", cateId);
    }

    @Transactional(readOnly = true)
    public Group findGroup(int clientId, int grpNo) {
        return groupRepository.findGroup(clientId, grpNo);
    }

    @Transactional(readOnly = true)
    public List<Group> findGroupAll(int clientId, String useYn) {
        return groupRepository.findGroupAll(clientId, useYn, null);
    }

    public int insertGroup(Group group) {
        return groupRepository.insertGroup(group);
    }

    public int updateGroup(Group group) {
        return groupRepository.updateGroup(group);
    }

    public int deleteGroup(Integer clientId, Integer grpNo) {
        int affected = groupRepository.deleteGroup(clientId, grpNo);
        if (affected > 0) {
            groupRepository.deleteGroupMappingsByGrpNo(clientId, grpNo);
        }

        return affected;
    }

    @Transactional(readOnly = true)
    public Page<GroupMappingResponseDto> findGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable) {
        //PagesUtils.setPageableIfNotNull(pageable);

        if (groupMappingRequestDto.isNoPageable()) {
            pageable = null;
        }

        return groupRepository.findGroupMappingsByGrpNo(groupMappingRequestDto, pageable);
    }

    @Transactional(readOnly = true)
    public void downloadExcelGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable, ResultHandler<?> resultHandler) {
        //PagesUtils.setPageableIfNotNull(pageable);
        groupRepository.findGroupMappingsByGrpNo(groupMappingRequestDto, pageable, resultHandler);
    }

    public int insertGroupMappings(List<GroupMapping> groupMappings) {
        return groupRepository.insertGroupMappings(groupMappings);
    }

    public int mergeGroupMappings(Integer clientId, List<GroupMapping> groupMappings, List<GroupMapping> deleteGroupMapping) {
        int affected = 0;
        if (deleteGroupMapping != null && deleteGroupMapping.size() > 0) {

            List<Integer> deleteGroupMappingNos = new ArrayList<>();
            deleteGroupMapping.forEach(groupMapping -> {
                deleteGroupMappingNos.add(groupMapping.getMapNo());
            });

            affected += groupRepository.deleteGroupMappings(clientId, deleteGroupMappingNos);
        }

        if (groupMappings != null && groupMappings.size() > 0) {
            affected += groupRepository.insertGroupMappings(groupMappings);
        }

        return affected;
    }

    public int deleteGroupMappings(Integer clientId, List<GroupMapping> deleteGroupMapping) {
        if (deleteGroupMapping != null && deleteGroupMapping.size() > 0) {
            List<Integer> deleteGroupMappingNos = new ArrayList<>();
            deleteGroupMapping.forEach(groupMapping -> {
                deleteGroupMappingNos.add(groupMapping.getMapNo());
            });

            return groupRepository.deleteGroupMappings(clientId, deleteGroupMappingNos);
        }
        return 0;
    }

    public int deleteAppGroupMappings(Integer clientId, List<AppGroupMapping> deleteAppGroupMapping) {
        if (deleteAppGroupMapping != null && deleteAppGroupMapping.size() > 0) {
            List<Integer> deleteGroupMappingNos = new ArrayList<>();
            deleteAppGroupMapping.forEach(groupMapping -> {
                deleteGroupMappingNos.add(groupMapping.getMapNo());
            });

            return groupRepository.deleteAppGroupMappings(clientId, deleteGroupMappingNos);
        }
        return 0;
    }


    @Transactional(readOnly = true)
    public Page<GroupMappingHistoryResponseDto> findGroupMappingsHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable) {
        PagesUtils.setPageableIfNotNull(pageable);
        return groupRepository.findGroupMappingsHistory(groupMappingHistoryRequestDto, pageable);
    }

    public int insertGroupMappingsHistory(List<GroupMappingHistory> groupMappingHistories) {
        return groupMappingHistories.stream().mapToInt(groupRepository::insertGroupMappingsHistory).sum();
    }

    @Transactional(readOnly = true)
    public void downloadExcelHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable, ResultHandler<?> resultHandler) {
        //excel은 setPageableIfNotNull을 하지 않는다. 하지만 sorting은 사용할 수 있기 때문에 여전히 필요하다.
        groupRepository.findGroupMappingsHistory(groupMappingHistoryRequestDto, pageable, resultHandler);
    }


    @Transactional(readOnly = true)
    public List<AppGroup> selectAppGroup(Integer clientId, Integer cateId) {
        return groupRepository.selectAppGroup(clientId, cateId);
    }

    public int insertAppGroup(AppGroup appGroup, AppUser appUser) {
        appGroup.setRegDt(LocalDateTime.now());
        appGroup.setRegUserNo(appUser.getId());
        appGroup.setClientId(appUser.getClientId());
        return groupRepository.insertAppGroup(appGroup);
    }

    public int updateAppGroup(AppGroup appGroup, AppUser appUser) {
        appGroup.setUpdDt(LocalDateTime.now());
        appGroup.setUpdUserNo(appUser.getId());
        appGroup.setClientId(appUser.getClientId());
        return groupRepository.updateAppGroup(appGroup);
    }

    public int deleteAppGroup(AppGroup appGroup, AppUser appUser) {
        int len = groupRepository.selectUsedAppGroupMapping(appUser.getClientId(), appGroup.getAppGrpNo());
        if (len > 0) {
            throw new BusinessException("사용중인 어플리케이션 그룹은 삭제할수 없습니다.", ErrorCode.CANNOT_DELETE);
        }
        return groupRepository.deleteAppGroup(appUser.getClientId(), appGroup.getAppGrpNo());
    }

    @Transactional(readOnly = true)
    public List<AppGroupMapping> selectAppGroupMapping(Integer clientId, Integer appGrpNo) {
        return groupRepository.selectAppGroupMapping(clientId, appGrpNo);
    }

    public int insertAppGroupMapping(AppUser appUser, AppGroupMapping appGroupMapping) {
        appGroupMapping.setRegDt(LocalDateTime.now());
        appGroupMapping.setRegUserNo(appUser.getId());
        return groupRepository.insertAppGroupMapping(appGroupMapping);
    }

    public int deleteAppGroupMapping(Integer appGrpNo) {
        return groupRepository.deleteAppGroupMapping(appGrpNo);
    }

    public void updateAppGroupMapping(AppUser appUser, Integer appGrpNo, List<AppGroupMapping> appGroupMappings) {

        List<AppGroupMapping> currentMappings = groupRepository.selectAppGroupMapping(appUser.getClientId(), appGrpNo);

        List<AppGroupMapping> createMappings = new ArrayList<>();
        List<AppGroupMapping> deleteMappings = new ArrayList<>(currentMappings);
        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();

        groupRepository.deleteAppGroupMapping(appGrpNo);
        if (appGroupMappings == null || appGroupMappings.size() == 0) {
            return;
        }
        for (AppGroupMapping appGroupMapping : appGroupMappings) {
            appGroupMapping.setRegUserNo(appUser.getId());
            groupRepository.insertAppGroupMapping(appGroupMapping);

            deleteMappings.remove(appGroupMapping);
            if(!currentMappings.contains(appGroupMapping)) {
                createMappings.add(appGroupMapping);
            }
        }

        // null 체거
        CollectionUtils.filter(deleteMappings, PredicateUtils.notNullPredicate());

        groupMappingHistories.addAll(this.genAppGroupMappingHistories(appUser, createMappings, "C"));
        groupMappingHistories.addAll(this.genAppGroupMappingHistories(appUser, deleteMappings, "D"));

        if (groupMappingHistories.size() > 0) {
            this.insertGroupMappingsHistory(groupMappingHistories);
        }
    }

    public void updateGroupMapping(AppUser appUser, Integer grpNo, List<GroupMapping> groupNewMappings) {

        GroupMappingRequestDto groupMappingRequestDto = new GroupMappingRequestDto();
        groupMappingRequestDto.setClientId(appUser.getClientId());
        groupMappingRequestDto.setGrpNo(grpNo);
        List<GroupMapping> currentMappings
                = this.findGroupMappingsByGrpNo(groupMappingRequestDto, null)
                .stream().map(e -> {
                    GroupMapping _groupMapping = new GroupMapping();
                    _groupMapping.setGrpNo(e.getGrpNo());
                    _groupMapping.setDataGbn(e.getDataGbn());
                    _groupMapping.setDataNo(e.getDataNo());
                    return _groupMapping;
                }).collect(Collectors.toList());

        List<GroupMapping> createMappings = new ArrayList<>();
        List<GroupMapping> deleteMappings = new ArrayList<>(currentMappings);
        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();


        for (GroupMapping groupMapping : groupNewMappings) {
            groupMapping.setRegEmpNo(appUser.getId());
            groupMapping.setGrpNo(grpNo);
            groupMapping.setRegDt(LocalDateTime.now());
            deleteMappings.remove(groupMapping);
            if(!currentMappings.contains(groupMapping)) {
                createMappings.add(groupMapping);
            }
        }
        // null 체거
        CollectionUtils.filter(deleteMappings, PredicateUtils.notNullPredicate());

        groupRepository.deleteGroupMappingsByGrpNo(appUser.getClientId(), grpNo);
        if(groupNewMappings.size() > 0) {
            groupRepository.insertGroupMappings(groupNewMappings);
        }


        groupMappingHistories.addAll(this.genGroupMappingHistories(appUser, createMappings, "C"));
        groupMappingHistories.addAll(this.genGroupMappingHistories(appUser, deleteMappings, "D"));

        if (groupMappingHistories.size() > 0) {
            this.insertGroupMappingsHistory(groupMappingHistories);
        }

    }

    public List<GroupMappingHistory> genGroupMappingHistories(AppUser appUser, List<GroupMapping> groupMappings, String action) {
        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String ip = HttpUtils.getRemoteAddr();
        groupMappings.forEach(groupMapping -> {
            GroupMappingHistory groupMappingHistory = new GroupMappingHistory();

            groupMappingHistory.of(groupMapping);
            groupMappingHistory.setAction(action);
            groupMappingHistory.setRegEmpNo(appUser.getId());
            groupMappingHistory.setRegDt(now);
            groupMappingHistory.setRegIp(ip);
            groupMappingHistory.setClientId(appUser.getClientId());

            if (groupMappingHistory.getMapNo() == null) {
                groupMappingHistory.setMapNo(0);
            }

            groupMappingHistories.add(groupMappingHistory);
        });

        return groupMappingHistories;
    }

    public List<GroupMappingHistory> genAppGroupMappingHistories(AppUser appUser, List<AppGroupMapping> groupMappings, String action) {
        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String ip = HttpUtils.getRemoteAddr();
        groupMappings.forEach(groupMapping -> {
            GroupMappingHistory groupMappingHistory = new GroupMappingHistory();

            groupMappingHistory.of(groupMapping);
            groupMappingHistory.setAction(action);
            groupMappingHistory.setRegEmpNo(appUser.getId());
            groupMappingHistory.setRegDt(now);
            groupMappingHistory.setRegIp(ip);
            groupMappingHistory.setClientId(appUser.getClientId());

            if (groupMappingHistory.getMapNo() == null) {
                groupMappingHistory.setMapNo(0);
            }

            groupMappingHistories.add(groupMappingHistory);
        });

        return groupMappingHistories;
    }
}

