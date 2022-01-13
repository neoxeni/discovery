package com.mercury.discovery.base.users.model;

import com.mercury.discovery.base.group.model.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Alias("UserGroup")
@Data
public class UserGroup extends Group {
    private Long targetId;
    private String target;
    private Long groupMappingId;
}
