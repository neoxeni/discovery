package com.mercury.discovery.base.users.service;

import com.mercury.discovery.base.BaseTopic;
import com.mercury.discovery.base.organization.service.OrganizationRepository;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserGroup;
import com.mercury.discovery.config.websocket.message.MessagePublisher;
import com.mercury.discovery.utils.IDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserService {

    private final CacheManager cacheManager;

    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;

    private final MessagePublisher messagePublisher;

    @Transactional(readOnly = true)
    public AppUser findById(Integer id) {
        AppUser appUser = userRepository.findById(id);
        setAppUserRoles(appUser);
        return appUser;
    }

    @Transactional(readOnly = true)
    public AppUser findByUserKey(String userKey) {
        AppUser appUser = userRepository.findByUserKey(userKey);
        setAppUserRoles(appUser);
        return appUser;
    }

    public AppUser getUser(String userKey) {
        return getUser(-1, userKey);
    }

    @Transactional(readOnly = true)
    public AppUser getUser(int clientId, String userKey) {
        AppUser appUser = userRepository.findByUserKey(userKey);
        setAppUserRoles(appUser);
        return appUser;
        /*AppUser appUser = null;
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            String cacheKey = "";
            if (clientId > -1) {
                cacheKey = clientId + ":" + userKey;
            } else {
                cacheKey = userKey;
            }

            appUser = cache.get(cacheKey, AppUser.class);
            if (appUser == null) {
                appUser = userRepository.findByUserKey(userKey);
                if (appUser != null) {
                    setAppUserRoles(appUser);
                }
                cache.put(cacheKey, appUser);
            }
        }

        return appUser;*/
    }

    @Transactional(readOnly = true)
    public void setAppUserRoles(AppUser appUser) {
        List<UserGroup> groups = userRepository.findGroupsByUserId(appUser.getId());
        if (appUser.getDepartmentId() != null) {
            List<UserGroup> departmentsGroups = organizationRepository.findDepartmentGroups(appUser.getClientId(), appUser.getDepartmentId());
            groups.addAll(departmentsGroups);
        }
        appUser.setGroups(groups);
    }

    public void cacheUser(AppUser appUser) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            String userKey = appUser.getUserKey();
            Integer clientId = appUser.getClientId();

            String cacheKey = clientId + ":" + userKey;
            cache.put(cacheKey, appUser);

            messagePublisher.convertAndSend(BaseTopic.USER.getBindTopic(userKey), appUser);
            messagePublisher.convertAndSend(BaseTopic.USERS.getBindTopic(String.valueOf(clientId)), appUser);
        }
    }

    public int insert(AppUser appUser) {
        if (appUser.getUserKey() == null) {
            appUser.setUserKey(IDGenerator.getUUID());
        }

        if (appUser.getPassword() == null) {
            appUser.setPassword("NO_PASS");
        }

        int affected = userRepository.insert(appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.insertLogin(appUser);
        return affected;
    }

    public int update(AppUser appUser) {
        return userRepository.update(appUser);
    }

    public int delete(AppUser appUser) {
        return userRepository.delete(appUser.getClientId(), appUser.getId());
    }


    public void updateLoginInfo(AppUser appUser) {
        //성공시 사용자 정보업데이트 passwordErrCount, lastLoginAt, lastIpAddress 등등
        userRepository.updateLoginInfo(appUser);
        userRepository.insertLoginHistory(appUser);
    }

    public void updateLogoutInfo(AppUser appUser) {
        appUser.setLastLogoutAt(LocalDateTime.now());
        userRepository.updateLogoutInfo(appUser);
    }

    public int resetUserPassword(Integer empNo, String password) {
        String encPassword = passwordEncoder.encode(password);
        LocalDateTime now = LocalDateTime.now();
        return userRepository.resetPassword(empNo, encPassword, now);
    }

    public void plusPasswordErrorCount(String username) {
        userRepository.plusPasswordErrorCount(username);
    }

    public void sendActiveSignal(String userKey, String uuid) {
        messagePublisher.convertAndSend(BaseTopic.ACTIVE.getBindTopic(userKey), uuid);
    }
}
