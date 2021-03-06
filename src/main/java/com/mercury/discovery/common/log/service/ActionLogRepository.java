package com.mercury.discovery.common.log.service;

import com.mercury.discovery.common.log.security.model.ActionLog;
import com.mercury.discovery.common.log.security.model.ActionLogRequestDto;
import com.mercury.discovery.common.log.security.model.ActionLogResponseDto;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ActionLogRepository {
    Page<ActionLogResponseDto> findAll(ActionLogRequestDto actionLogRequestDto, Pageable pageable);

    void findAll(ActionLogRequestDto actionLogRequestDto, Pageable pageable, ResultHandler<?> resultHandler);

    ActionLogResponseDto findOne(Integer seqNo);

    int insert(ActionLog actionLog);

    int delete(ActionLog actionLog);

    int update(ActionLog actionLog);
}
