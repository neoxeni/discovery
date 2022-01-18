package com.mercury.discovery.base.users.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Set;

@Alias("TokenUser")
@Data
@EqualsAndHashCode(of = {"id"})
public class TokenUser implements Serializable {
    private static final long serialVersionUID = -3098368111659804213L;
    private Integer id;
    private UserType userType;
    private String userKey;
    private String name;
    private Integer clientId;

    private Set<String> roles;
    private String token;

    public boolean hasAnyRole(String... rolesStr) {
        if (roles == null) {
            return false;
        }

        for (String role : rolesStr) {
            if (roles.contains("ROLE_" + role) || roles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
