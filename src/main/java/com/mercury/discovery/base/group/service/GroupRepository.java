package com.mercury.discovery.base.group.service;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.group.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GroupRepository {
    List<Group> findGroupAll(int clientId, String useYn, Integer cateId);

    Group findGroup(int clientId, Long id);

    int insertGroup(Group group);

    int updateGroup(Group group);

    int deleteGroup(Integer clientId, Long groupId);

    Page<GroupMappingResponseDto> findGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable);

    void findGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable, ResultHandler<?> resultHandler);

    int insertGroupMappings(List<GroupMapping> groupMappings);

    int deleteGroupMappings(Integer clientId, List<Long> groupMappingNos);

    void deleteGroupMappingsByGroupId(Integer clientId, Long groupId);

    int insertGroupMappingsHistory(GroupMappingHistory groupMappingHistory);

    Page<GroupMappingHistoryResponseDto> findGroupMappingsHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable);

    void findGroupMappingsHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable, ResultHandler<?> resultHandler);
}
