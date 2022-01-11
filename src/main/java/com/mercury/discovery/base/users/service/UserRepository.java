package com.mercury.discovery.base.users.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.AppUserRequestDto;
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
    AppUser findByUserIdForLogin(String userId, String cmpnyId);

    //일반적인 사용자 검색
    List<AppUser> find(AppUserRequestDto appUserRequestDto);

    List<UserRole> findRolesByEmpNo(int empNo);

    Set<UserAppRole> findAppRolesByEmpNo(int empNo);

    AppUser findByUserKey(String userKey);

    AppUser findByUserId(String userId);

    int insert(AppUser appUser);

    int insertLogin(AppUser appUser);

    int insertLoginHistory(AppUser appUser);

    int insertLogoutHistory(AppUser appUser);

    int update(AppUser appUser);

    int updateLoginInfo(AppUser appUser);

    int updateLogoutInfo(AppUser appUser);

    int updateLoginId(String userId, int empNo);

    int plusPsswdErrNum(AppUser appUser);

    int delete(Integer cmpnyNo, Integer empNo);

    int resetPassword(Integer empNo, String psswd, LocalDateTime psswdUpdDt);

    int resetPasswordCnt(Integer empNo);

    AppUser findByUserEmail(String appUser);

    String getEmail(String email, String cmpnyId);

    String getUserId(String userId, String cmpnyId);

    String getEmailForDomain(String email);

    AppUser findByUserEmailWithCmpnyId(String email, String cmpnyId);

    List<AppUser> findDeptEmpList(int cmpnyNo, String deptCd, String rtrmntYn);
}
