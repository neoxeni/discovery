package com.mercury.discovery.base.log.service;

import com.github.pagehelper.Page;
import com.mercury.discovery.base.log.model.ActionLog;
import com.mercury.discovery.base.log.model.ActionLogRequestDto;
import com.mercury.discovery.base.log.model.ActionLogResponseDto;
import com.mercury.discovery.common.log.security.SecurityLog;
import com.mercury.discovery.util.PagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ActionLogService {
    private final ActionLogRepository actionLogRepository;

    @Transactional(readOnly = true)
    public Page<ActionLogResponseDto> findAll(ActionLogRequestDto actionLogRequestDto, Pageable pageable) {
        PagesUtils.setPageableIfNotNull(pageable);
        return actionLogRepository.findAll(actionLogRequestDto, pageable);
    }

    @Transactional(readOnly = true)
    public void downloadExcel(ActionLogRequestDto actionLogRequestDto, Pageable pageable, ResultHandler<?> resultHandler) {
        //excel은 setPageableIfNotNull을 하지 않는다. 하지만 sorting은 사용할 수 있기 때문에 여전히 필요하다.
        actionLogRepository.findAll(actionLogRequestDto, pageable, resultHandler);
    }

    public int save(ActionLog actionLog) {
        if (actionLog.getSeqNo() == null) {
            return actionLogRepository.insert(actionLog);
        } else {
            return actionLogRepository.update(actionLog);
        }
    }

    public int save(SecurityLog securityLog) {
        ActionLog actionLog = transToActionLog(securityLog);
        return save(actionLog);
    }

    public int delete(ActionLog actionLog) {
        return actionLogRepository.delete(actionLog);
    }


    private ActionLog transToActionLog(SecurityLog securityLog){
        ActionLog actionLog = new ActionLog();

        actionLog.setSeqNo(securityLog.getSeqNo());

        actionLog.setEmpNo(securityLog.getEmpNo());
        actionLog.setIp(securityLog.getIp());
        actionLog.setRegDt(securityLog.getRegDt());

        actionLog.setMenu(securityLog.getMenu());
        actionLog.setSubMenu(securityLog.getSubMenu());
        actionLog.setAction(securityLog.getAction());
        actionLog.setActionUrl(securityLog.getActionUrl());
        actionLog.setInputVal(securityLog.getInputVal());

        actionLog.setRegNation(securityLog.getRegNation());
        actionLog.setEtc1(securityLog.getEtc1());
        actionLog.setEtc2(securityLog.getEtc2());
        actionLog.setEtc3(securityLog.getEtc3());
        actionLog.setEtc4(securityLog.getEtc4());
        actionLog.setEtc5(securityLog.getEtc5());
        actionLog.setDivCd(securityLog.getDivCd());
        actionLog.setCmpnyNo(securityLog.getCmpnyNo());

        return actionLog;
    }
}
