package com.mercury.discovery.base.users.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserAppRole;
import com.mercury.discovery.base.users.model.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Mapper
@Repository
public interface UserRepository {
    //로그인 전용으로 password 정보를 포함한다.
    AppUser findByUserIdForLogin(String userId, String clientId);

    List<UserRole> findRolesByEmpNo(int empNo);

    Set<UserAppRole> findAppRolesByEmpNo(int empNo);

    AppUser findByUserKey(String userKey);

    AppUser findByUserId(Integer id);

    int insert(AppUser appUser);

    int insertLogin(AppUser appUser);

    int insertLoginHistory(AppUser appUser);

    int update(AppUser appUser);

    int updateLoginInfo(AppUser appUser);

    int updateLogoutInfo(AppUser appUser);

    int updateLoginId(String userId, int empNo);

    int plusPasswordErrorCount(AppUser appUser);

    int delete(Integer clientId, Integer empNo);

    int resetPassword(Integer empNo, String psswd, LocalDateTime psswdUpdDt);

    int resetPasswordCount(Integer empNo);

    AppUser findByUserEmail(String appUser);


}
