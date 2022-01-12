package com.mercury.discovery.common.log.security;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.log.security.model.Action;
import com.mercury.discovery.common.log.security.model.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Stream;

@Slf4j
@Component
@DependsOn(value = {"ContextUtils"})
@RequiredArgsConstructor
@Aspect
public class SecurityLogAop {


    private final Environment environment;
    private final SecurityLogService securityLogService;
    private final ApplicationContext applicationContext;


    @Value("${apps.actionLogInfoFile:classpath:action-log.xml}")
    private String actionLogInfoFile;
    private Configuration configuration;
    private AntPathMatcher antPathMatcher;


    @PostConstruct
    public void init() {
        try {
            antPathMatcher = new AntPathMatcher();
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            URL resource = ResourceUtils.getURL(actionLogInfoFile);
            File physicalFile = null;
            if (resource.getProtocol().equals("file")) {
                physicalFile = ResourceUtils.getFile(actionLogInfoFile);
            }

            if (physicalFile == null) {
                Resource stateResource = applicationContext.getResource(actionLogInfoFile);
                try (InputStream is = stateResource.getInputStream()) {
                    configuration = (Configuration) unmarshaller.unmarshal(is);
                }
            } else {
                log.info("actionLogInfoFile = {}, isFile = {}", physicalFile, physicalFile.isFile());
                if (physicalFile.exists() && physicalFile.isFile()) {
                    configuration = (Configuration) unmarshaller.unmarshal(physicalFile);
                }
            }
            if (configuration != null && configuration.getRequestParam() != null) {
                securityLogService.setInputValueSerializationInclusion(configuration.getRequestParam().getIncludeValue());
            }

            log.info("[SecurityLogAop] Configuration\n{}", configuration);

        } catch (IOException | JAXBException ioe) {
            log.warn("actionLogInfoFile is invalid. file={}\nerror={}", actionLogInfoFile, ioe);
        }

    }

    //@Around("@within(SecurityLogging) || @annotation(SecurityLogging)")
    @Around("execution(@org.springframework.web.bind.annotation.GetMapping public * *(..)) " +
            "|| execution(@org.springframework.web.bind.annotation.PostMapping public * *(..)) " +
            "|| execution(@org.springframework.web.bind.annotation.PatchMapping public * *(..)) " +
            "|| execution(@org.springframework.web.bind.annotation.DeleteMapping public * *(..)) " +
            "|| execution(@org.springframework.web.bind.annotation.PutMapping public * *(..))")
    public Object logPerf(ProceedingJoinPoint joinPoint) throws Throwable {

        if (configuration == null) {
            return joinPoint.proceed();
        }

        boolean acceptsProfiles;
        if (configuration.getActiveProfiles() == null || configuration.getActiveProfiles().length == 0) {
            acceptsProfiles = false;
        } else if (configuration.getActiveProfiles()[0].equals("*")) {
            acceptsProfiles = true;
        } else {
            acceptsProfiles = environment.acceptsProfiles(Profiles.of(configuration.getActiveProfiles()));
        }

        if (!configuration.isActive() || !acceptsProfiles) {
            return joinPoint.proceed();
        }

        Action requestAction = getRequestAction(joinPoint, joinPoint.getTarget().getClass());
        if (configuration.isExclude(antPathMatcher, requestAction)) {
            return joinPoint.proceed();
        }

        requestAction.setMeta(configuration.getMeta(requestAction));

        SecurityLog securityLog = new SecurityLog();
        securityLog.setCreatedAt(LocalDateTime.now());

        Object retVal = null;
        try {
            retVal = joinPoint.proceed();    // 메서드 호출
            return retVal;
        } catch (Throwable t) {
            retVal = t;
            throw t;
        } finally {
            setFromRequest(securityLog);
            securityLogService.startSecurityLog(configuration, joinPoint, securityLog, requestAction, retVal);
        }
    }

    private void setFromRequest(SecurityLog securityLog) {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest req = requestAttributes.getRequest();
            String menuName = (String) req.getAttribute(SecurityLogContext.MENU_NAME);
            if (menuName == null) {
                menuName = (String) req.getAttribute("sitemenu:pageMenuName");//ubicus-sitemenu 사용시..
            }

            if (menuName != null) {
                securityLog.setMenu(menuName);
            }

            securityLog.setAction(req.getMethod());
            String url = req.getRequestURI();
            if (req.getQueryString() != null) {
                url += "?" + req.getQueryString();
            }
            securityLog.setActionUrl(url);

            String remoteAddr = req.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
                remoteAddr = "127.0.0.1";
            }
            securityLog.setIp(remoteAddr);

            Locale loc = LocaleContextHolder.getLocale();
            securityLog.setLanguage(loc.getLanguage());

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if ((principal instanceof UserDetails)) {
                AppUser appUser = (AppUser) principal;
                securityLog.setUserId(appUser.getId());
            }
        }
    }

    private Action getRequestAction(JoinPoint joinPoint, Class clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping == null ? "" : requestMapping.value()[0];

        Action action = Stream.of(GetMapping.class, PutMapping.class, PostMapping.class,
                PatchMapping.class, DeleteMapping.class, RequestMapping.class)
                .filter(mappingClass -> method.isAnnotationPresent(mappingClass))
                .map(mappingClass -> getAction(method, mappingClass, baseUrl))
                .findFirst().orElse(null);

        return action;
    }

    /* httpMETHOD + requestURI 를 반환 */
    private Action getAction(Method method, Class<? extends Annotation> annotationClass, String baseUrl) {
        Annotation annotation = method.getAnnotation(annotationClass);
        String[] value;
        String httpMethod;
        try {
            value = (String[]) annotationClass.getMethod("value").invoke(annotation);
            httpMethod = (annotationClass.getSimpleName().replace("Mapping", "")).toUpperCase();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            return null;
        }

        String subUrl = (value.length > 0 ? value[0] : "");

        Action action = new Action();
        action.setMethod(httpMethod);
        action.setPath(environment.resolveRequiredPlaceholders(baseUrl) + environment.resolveRequiredPlaceholders(subUrl));
        return action;
    }
}
