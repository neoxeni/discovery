package com.mercury.discovery.base.log.model;

import com.mercury.discovery.common.log.security.SecurityLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * tb_cmm_admin_log
 */

@EqualsAndHashCode(callSuper = true)
@Alias("ActionLog")
@Data
public class ActionLog extends SecurityLog {

}
