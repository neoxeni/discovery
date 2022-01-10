package com.mercury.discovery.common.log.security.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * tb_cmm_admin_log
 */

@EqualsAndHashCode(callSuper = true)
@Alias("ActionLogResponseDto")
@Data
public class ActionLogResponseDto extends ActionLog{
    private String userNm;
    private String userId;
}
