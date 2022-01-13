package com.mercury.discovery.base.group.service;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.group.model.*;
import com.mercury.discovery.base.users.model.AppUser;
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
    public Group findGroup(int clientId, Long id) {
        return groupRepository.findGroup(clientId, id);
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

    public int deleteGroup(Integer clientId, Long grpNo) {
        return groupRepository.deleteGroup(clientId, grpNo);
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

            List<Long> deleteGroupMappingNos = new ArrayList<>();
            deleteGroupMapping.forEach(groupMapping -> {
                deleteGroupMappingNos.add(groupMapping.getId());
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
            List<Long> deleteGroupMappingNos = new ArrayList<>();
            deleteGroupMapping.forEach(groupMapping -> {
                deleteGroupMappingNos.add(groupMapping.getId());
            });

            return groupRepository.deleteGroupMappings(clientId, deleteGroupMappingNos);
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

    public void updateGroupMapping(AppUser appUser, Long grpNo, List<GroupMapping> groupNewMappings) {

        GroupMappingRequestDto groupMappingRequestDto = new GroupMappingRequestDto();
        groupMappingRequestDto.setClientId(appUser.getClientId());
        groupMappingRequestDto.setGrpNo(grpNo);
        List<GroupMapping> currentMappings
                = this.findGroupMappingsByGrpNo(groupMappingRequestDto, null)
                .stream().map(e -> {
                    GroupMapping _groupMapping = new GroupMapping();
                    _groupMapping.setGroupId(e.getGroupId());
                    _groupMapping.setTarget(e.getTarget());
                    _groupMapping.setTargetId(e.getTargetId());
                    return _groupMapping;
                }).collect(Collectors.toList());

        List<GroupMapping> createMappings = new ArrayList<>();
        List<GroupMapping> deleteMappings = new ArrayList<>(currentMappings);
        List<GroupMappingHistory> groupMappingHistories = new ArrayList<>();


        for (GroupMapping groupMapping : groupNewMappings) {
            groupMapping.setCreatedBy(appUser.getId());
            groupMapping.setGroupId(grpNo);
            groupMapping.setCreatedAt(LocalDateTime.now());
            deleteMappings.remove(groupMapping);
            if (!currentMappings.contains(groupMapping)) {
                createMappings.add(groupMapping);
            }
        }
        // null 체거
        CollectionUtils.filter(deleteMappings, PredicateUtils.notNullPredicate());

        groupRepository.deleteGroupMappingsByGrpNo(appUser.getClientId(), grpNo);
        if (groupNewMappings.size() > 0) {
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
            groupMappingHistory.setCreatedBy(appUser.getId());
            groupMappingHistory.setCreatedAt(now);
            groupMappingHistory.setRegIp(ip);
            groupMappingHistory.setClientId(appUser.getClientId());

            if (groupMappingHistory.getGroupMappingId() == null) {
                groupMappingHistory.setGroupMappingId(0L);
            }

            groupMappingHistories.add(groupMappingHistory);
        });

        return groupMappingHistories;
    }


}

