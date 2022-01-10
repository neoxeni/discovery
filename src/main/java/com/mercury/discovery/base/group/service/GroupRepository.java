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
    List<Group> findGroupAll(int cmpnyNo, String useYn, Integer cateId);

    Group findGroup(int cmpnyNo, int grpNo);

    int insertGroup(Group group);

    int updateGroup(Group group);

    int deleteGroup(Integer cmpnyNo, Integer grpNo);

    Page<GroupMappingResponseDto> findGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable);

    void findGroupMappingsByGrpNo(GroupMappingRequestDto groupMappingRequestDto, Pageable pageable, ResultHandler<?> resultHandler);

    int insertGroupMappings(List<GroupMapping> groupMappings);

    int deleteGroupMappings(Integer cmpnyNo, List<Integer> groupMappingNos);

    int deleteAppGroupMappings(Integer cmpnyNo, List<Integer> groupMappingNos);

    void deleteGroupMappingsByGrpNo(Integer cmpnyNo, Integer grpNo);

    int insertGroupMappingsHistory(GroupMappingHistory groupMappingHistory);

    Page<GroupMappingHistoryResponseDto> findGroupMappingsHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable);

    void findGroupMappingsHistory(GroupMappingHistoryRequestDto groupMappingHistoryRequestDto, Pageable pageable, ResultHandler<?> resultHandler);



    List<AppGroup> selectAppGroup(Integer cmpnyNo, Integer cateId);
    int insertAppGroup(AppGroup appGroup);
    int updateAppGroup(AppGroup appGroup);
    int deleteAppGroup(Integer cmpnyNo, Integer appGrpNo);
    List<AppGroupMapping> selectAppGroupMapping(Integer cmpnyNo, Integer appGrpNo);
    int selectUsedAppGroupMapping(Integer cmpnyNo, Integer appGrpNo);
    int insertAppGroupMapping(AppGroupMapping appGroupMapping);
    int deleteAppGroupMapping(Integer appGrpNo);

}
