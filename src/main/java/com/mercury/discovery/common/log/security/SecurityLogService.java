package com.mercury.discovery.common.log.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercury.discovery.base.log.service.ActionLogService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.log.security.model.Action;
import com.mercury.discovery.common.log.security.model.Configuration;
import com.mercury.discovery.common.log.security.model.Meta;
import com.mercury.discovery.common.log.security.model.RequestParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityLogService {
    private ObjectMapper mapper;

    private final ActionLogService actionLogService;

    @PostConstruct
    public void init(){
        mapper = new ObjectMapper();
    }

    public void setInputValueSerializationInclusion(String include) {
        JsonInclude.Include jsonInclude = JsonInclude.Include.NON_NULL;
        if(StringUtils.hasLength(include)) {
            try {
                jsonInclude = JsonInclude.Include.valueOf(include);
            }catch (Exception ignore) {
                log.warn("setInputValueSerializationInclusion value is not value. [] = {}", include);
            }
        }

        mapper.setSerializationInclusion(jsonInclude);



    }

    @Async
    public void startSecurityLog(
            Configuration configuration,
            ProceedingJoinPoint joinPoint,
            SecurityLog securityLog,
            Action requestAction,
            Object retVal) {
        try {
            long begin = System.currentTimeMillis();

            securityLog.setInputVal(getInput(configuration, joinPoint));

            /*String result;
            if (retVal instanceof Throwable) {//실패.. exception ex
                result = getException((Throwable) retVal);
            } else {  //성공
                result = getOutput(retVal);
            }*/

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            SecurityLogging securityLogging = method.getAnnotation(SecurityLogging.class);
            if (securityLogging == null) {
                securityLogging = method.getDeclaringClass().getAnnotation(SecurityLogging.class);
            }

            if (securityLogging != null) {
                if (!"menu".equals(securityLogging.menu())) {
                    securityLog.setMenu(securityLogging.menu());
                } else if (securityLog.getMenu() == null) {
                    securityLog.setMenu("menu");
                }

                securityLog.setSubMenu(securityLogging.subMenu());
                securityLog.setEtc1(securityLogging.etc1());
                securityLog.setEtc2(securityLogging.etc2());
                securityLog.setEtc3(securityLogging.etc3());
                securityLog.setEtc4(securityLogging.etc4());
                securityLog.setEtc5(securityLogging.etc5());
                securityLog.setDivCd(securityLogging.divCd());
            }

            Meta requestActionMeta = requestAction.getMeta();
            if (requestActionMeta != null) {
                if (StringUtils.hasLength(requestActionMeta.getMenu())) {
                    securityLog.setMenu(requestActionMeta.getMenu());
                }
                if (StringUtils.hasLength(requestActionMeta.getSubmenu())) {
                    securityLog.setSubMenu(requestActionMeta.getSubmenu());
                }
                if (StringUtils.hasLength(requestActionMeta.getDiv())) {
                    securityLog.setDivCd(requestActionMeta.getDiv());
                }
                if (StringUtils.hasLength(requestActionMeta.getEtc1())) {
                    securityLog.setEtc1(requestActionMeta.getEtc1());
                }
                if (StringUtils.hasLength(requestActionMeta.getEtc2())) {
                    securityLog.setEtc2(requestActionMeta.getEtc2());
                }
                if (StringUtils.hasLength(requestActionMeta.getEtc3())) {
                    securityLog.setEtc3(requestActionMeta.getEtc3());
                }
                if (StringUtils.hasLength(requestActionMeta.getEtc4())) {
                    securityLog.setEtc4(requestActionMeta.getEtc4());
                }
                if (StringUtils.hasLength(requestActionMeta.getEtc5())) {
                    securityLog.setEtc5(requestActionMeta.getEtc5());
                }
            }

            if(securityLog.getCmpnyNo() == null) {
                log.warn("securityLog cmpnyNo null => {}", securityLog);
                return;
            }

            actionLogService.save(securityLog);

            if (log.isDebugEnabled()) {
                log.debug("{}ms \n{}", System.currentTimeMillis() - begin, securityLog);
            }
        } catch (Exception e) {
            log.error("startSecurityLog fail", e);
        }
    }


    private String getInput(Configuration configuration, ProceedingJoinPoint joinPoint) {
        try {

            RequestParam requestParam = configuration.getRequestParam();

            if (requestParam == null || !requestParam.isSave()) {
                return "{}";
            }

            String input = null;

            Map<String, Object> inputMap = new HashMap<>();

            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            final Class[] classes = signature.getParameterTypes();
            final String[] parameterNames = signature.getParameterNames();
            final Object[] args = joinPoint.getArgs();

            for (int i = 0, ic = args.length; i < ic; i++) {
                Object arg = args[i];
                if (arg instanceof MultipartRequest) {
                    //inputMap.put(parameterNames[i], "MultipartRequest");
                } else if (arg instanceof AppUser) {
                    //inputMap.put(parameterNames[i], "AppUser");
                } else {
                    inputMap.put(parameterNames[i], args[i]);
                }
            }

            String inputStr = mapper.writeValueAsString(inputMap);
            List<String> ignore = requestParam.getIgnore();
            if (CollectionUtils.isEmpty(ignore)) {
                return inputStr;
            } else {
                for (String keyword : ignore) {
                    inputStr = inputStr.replaceAll(keyword + "\":\\s*\"[^\"]+?([^\\/\"]+)",
                            keyword + "\":\"*");
                }
            }

            if(inputStr.length() > 3000) {
                // 3000자 넘으면 자른다...
                inputStr = inputStr.substring(0, 3000);
            }

            return inputStr;


        } catch (JsonProcessingException e) {
            return "{\"error\":\"extract input:" + e.getMessage() + "\"}";
        }
    }

    private String getOutput(Object retVal) {
        String output = "{}";
        try {
            Object retLog;
            if (retVal instanceof ResponseEntity) {
                retLog = ((ResponseEntity) retVal).getBody();
            } else {
                retLog = retVal;
            }

            if (retLog != null) {
                output = mapper.writeValueAsString(retLog);
            }

            return output;
        } catch (JsonProcessingException e) {
            return "{\"error\":\"extract output:" + e.getMessage() + "\"}";
        }
    }

    private String getException(Throwable t) {
        try {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            Map<String, String> map = new HashMap<>();
            map.put("exception", sw.toString());
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"extract exception:" + e.getMessage() + "\"}";
        }
    }
}
