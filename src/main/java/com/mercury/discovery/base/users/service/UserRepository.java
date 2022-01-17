package com.mercury.discovery.base.users.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserGroup;
import com.mercury.discovery.base.users.model.UserLogin;
import com.mercury.discovery.common.model.CamelMap;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserRepository {
    //로그인 전용으로 password 정보를 포함한다.
    UserLogin findByUsernameForLogin(String username, String clientId);

    List<UserGroup> findGroupsByUserId(Integer userId);

    AppUser findById(Integer id);

    AppUser findByUserKey(String userKey);

    int insert(AppUser appUser);

    int update(AppUser appUser);

    int delete(Integer clientId, Integer empNo);

    int insertLogin(AppUser appUser);

    int insertLoginHistory(AppUser appUser);

    int updateLoginInfo(AppUser appUser);

    int updateLogoutInfo(AppUser appUser);

    int plusPasswordErrorCount(String username);

    int resetPassword(Integer id, String password, LocalDateTime passwordUpdatedAt);
}
