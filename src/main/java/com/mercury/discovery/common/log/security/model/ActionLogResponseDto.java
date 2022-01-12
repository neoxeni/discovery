package com.mercury.discovery.common.log.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * cmm_action_log
 */

@EqualsAndHashCode(callSuper = true)
@Alias("ActionLogResponseDto")
@Data
public class ActionLogResponseDto extends ActionLog{
    private String name;
    private String username;
}
