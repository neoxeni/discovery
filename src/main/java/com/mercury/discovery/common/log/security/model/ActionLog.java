package com.mercury.discovery.common.log.security.model;

import com.mercury.discovery.common.log.security.SecurityLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * cmm_action_log
 */

@EqualsAndHashCode(callSuper = true)
@Alias("ActionLog")
@Data
public class ActionLog extends SecurityLog {

}
