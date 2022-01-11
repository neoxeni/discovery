package com.mercury.discovery.base.users.service;

import com.mercury.discovery.base.BaseTopic;
import com.mercury.discovery.base.organization.service.OrganizationRepository;
import com.mercury.discovery.base.users.model.*;
import com.mercury.discovery.config.websocket.message.MessagePublisher;
import com.mercury.discovery.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserService {

    private final CacheManager cacheManager;

    private final UserRepository userRepository;

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final MessagePublisher messagePublisher;

    @Value("${apps.mode:on-premise}")
    private String appMode;

    public String getCmpnyId(String cmpnyId) {
        //on-premise 모드시에는 voc로 cmpnyId가 고정된다.
        if ("on-premise".equals(appMode)) {
            return "voc";
        }

        return cmpnyId;
    }

    @Transactional(readOnly = true)
    public AppUser getUserForLogin(String userId, String cmpnyId) {
        return userRepository.findByUserIdForLogin(userId, getCmpnyId(cmpnyId));
    }


    @Nullable
    public String getApiToken(TokenUser tokenUser) {
        return jwtTokenProvider.getJwtFromUser(tokenUser, true);
    }


    @Transactional(readOnly = true)
    public void setAppUserRoles(AppUser appUser) {
        List<UserRole> roleList = getUserRoles(appUser);
        List<UserAppRole> appRoleList = getUserAppRoles(appUser);
        appUser.setRoles(roleList);
        appUser.setAppRoles(appRoleList);

        //spring security authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (appUser.getAuthorities() != null) {
            authorities.addAll(appUser.getAuthorities());
        }

        roleList.forEach(userRole -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getGrpCd()));
        });

        appRoleList.forEach(userRole -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_$APP$_" + userRole.getAppGrpCd()));
        });

        appUser.setAuthorities(authorities);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getUserRoles(AppUser appUser) {
        List<UserRole> roles = userRepository.findRolesByEmpNo(appUser.getEmpNo());
        if (appUser.getDeptNo() != null) {
            List<UserRole> departmentsRoles = organizationRepository.findDepartmentsRoles(appUser.getCmpnyNo(), appUser.getDeptNo());
            roles.addAll(departmentsRoles);
        }

        return roles;
    }

    @Transactional(readOnly = true)
    public List<UserAppRole> getUserAppRoles(AppUser appUser) {
        Set<UserAppRole> roles = userRepository.findAppRolesByEmpNo(appUser.getEmpNo());
        if (appUser.getDeptNo() != null) {
            Set<UserAppRole> departmentsRoles = organizationRepository.findDepartmentsAppRoles(appUser.getCmpnyNo(), appUser.getDeptNo());
            roles.addAll(departmentsRoles);
        }
        return new ArrayList<>(roles);

    }

    @Transactional(readOnly = true)
    @Nullable
    public AppUser getUser(AppUserRequestDto appUserRequestDto) {
        AppUser appUser = userRepository.find(appUserRequestDto).stream().findFirst().orElse(null);
        if (appUser != null) {
            setAppUserRoles(appUser);
        }

        return appUser;
    }

    public AppUser getUser(String userKey) {
        return getUser(-1, userKey);
    }

    @Transactional(readOnly = true)
    public AppUser getUser(int cmpnyNo, String userKey) {
        AppUser appUser = null;
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            String cacheKey = "";
            if (cmpnyNo > -1) {
                cacheKey = cmpnyNo + ":" + userKey;
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

        return appUser;
    }

    public void cacheUser(AppUser appUser) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            String userKey = appUser.getUserKey();
            Integer cmpnyNo = appUser.getCmpnyNo();

            String cacheKey = cmpnyNo + ":" + userKey;
            cache.put(cacheKey, appUser);

            messagePublisher.convertAndSend(BaseTopic.USER.getBindTopic(userKey), appUser);
            messagePublisher.convertAndSend(BaseTopic.USERS.getBindTopic(String.valueOf(cmpnyNo)), appUser);
        }
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#appUser.cmpnyNo.toString()"),
            @CacheEvict(cacheNames = "users", key = "#appUser.cmpnyNo.toString().concat(':').concat(#appUser.userKey)")

    })
    public int insert(AppUser appUser) {
        if (appUser.getPsswd() == null) {
            appUser.setPsswd(appUser.getId());
        }

        int affected = userRepository.insert(appUser);

        String password = passwordEncoder.encode(appUser.getPsswd());
        appUser.setPsswd(password);
        userRepository.insertLogin(appUser);
        return affected;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#appUser.cmpnyNo.toString()"),
            @CacheEvict(cacheNames = "users", key = "#appUser.cmpnyNo.toString().concat(':').concat(#appUser.userKey)")
    })
    public int update(AppUser appUser) {
        return userRepository.update(appUser);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "deptEmpListForTreeAll", key = "#appUser.cmpnyNo.toString()"),
            @CacheEvict(cacheNames = "users", key = "#appUser.cmpnyNo.toString().concat(':').concat(#appUser.userKey)")
    })
    public int delete(AppUser appUser) {
        return userRepository.delete(appUser.getCmpnyNo(), appUser.getEmpNo());
    }

    public int successLoginInfo(AppUser appUser) {
        userRepository.insertLoginHistory(appUser);
        return userRepository.updateLoginInfo(appUser);
    }

    public int failureLoginInfo(AppUser appUser) {
        return userRepository.plusPsswdErrNum(appUser);
    }

    public int updateLogoutInfo(AppUser appUser) {
        userRepository.insertLogoutHistory(appUser);
        appUser.setLastLogoutDt(LocalDateTime.now());
        return userRepository.updateLogoutInfo(appUser);
    }

    public int resetUserPassword(Integer empNo, String password) {
        String encPassword = passwordEncoder.encode(password);
        LocalDateTime now = LocalDateTime.now();
        return userRepository.resetPassword(empNo, encPassword, now);
    }

    public AppUser findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }


    @Transactional(readOnly = true)
    public String getEmailForDomain(String email) {
        return userRepository.getEmailForDomain(email);
    }

    public void updateLoginId(String userId, int empNo) {
        userRepository.updateLoginId(userId, empNo);
    }

    @Transactional(readOnly = true)
    public AppUser findByUserEmailWithCmpnyId(String email, String cmpnyId) {
        return userRepository.findByUserEmailWithCmpnyId(email, getCmpnyId(cmpnyId));
    }

    @Transactional(readOnly = true)
    public String getEmail(String email, String cmpnyId) {
        return userRepository.getEmail(email, cmpnyId);
    }

    @Transactional(readOnly = true)
    public String getUserId(String userId, String cmpnyId) {
        return userRepository.getUserId(userId, cmpnyId);
    }

    @Transactional(readOnly = true)
    public List<AppUser> findDeptEmpList(int cmpnyNo, String deptCd, String rtrmntYn) {
        return userRepository.findDeptEmpList(cmpnyNo, deptCd, rtrmntYn);
    }

    @Transactional(readOnly = true)
    public AppUser findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void sendActiveSignal(String userKey, String uuid) {
        messagePublisher.convertAndSend(BaseTopic.ACTIVE.getBindTopic(userKey), uuid);
    }

}
