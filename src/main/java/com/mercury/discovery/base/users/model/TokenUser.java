package com.mercury.discovery.base.users.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("TokenUser")
@Data
@EqualsAndHashCode(of = {"userNo"})
public class TokenUser implements Serializable {
    private static final long serialVersionUID = -3098368111659804213L;

    private UserType userType = UserType.NONE;
    private Integer cmpnyNo;
    private String userKey;

    private Integer userNo;//same as empNo
    private String id;
    private String name;

    //아래의 정보는 JWT 관련 정보
    private String jwt;
    private long createdAt;
    private long expiredAt;
}
