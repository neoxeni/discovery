package com.mercury.discovery.base.users.model;

import com.mercury.discovery.base.group.model.GroupType;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("UserGroup")
@Data
public class UserGroup {
    private Long id;
    private GroupType type;
    private String code;
    private String name;

    private String target;
    private Long targetId;
    private Long groupMappingId;
}
