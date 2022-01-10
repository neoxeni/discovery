package com.mercury.discovery.base.users.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("UserAppRole")
@Data
@EqualsAndHashCode(of = {"appGrpNo"})
public class UserAppRole implements Serializable {
    private Integer appGrpNo;
    private String appGrpCd;
    private String appGrpNm;
}
