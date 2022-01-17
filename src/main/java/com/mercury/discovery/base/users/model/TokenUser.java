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

    private UserType userType;

    private Integer clientId;
    private Integer id;
    private String userKey;
    private String name;
}
