package com.mercury.discovery.base.users.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("TokenUser")
@Data
@EqualsAndHashCode(of = {"id"})
public class TokenUser implements Serializable {
    private static final long serialVersionUID = -3098368111659804213L;

    private Integer id;
    private String name;
    private String userKey;
    private Integer clientId;

    //아래의 정보는 JWT 관련 정보
    private String jwt;
    private long issuedMillis;
    private long expiredMillis;
}
