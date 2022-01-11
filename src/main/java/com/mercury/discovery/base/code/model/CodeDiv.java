package com.mercury.discovery.base.code.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * tb_cmm_code_div
 */

@Alias("CodeDiv")
@Data
public class CodeDiv implements Serializable {
    private static final long serialVersionUID = -5739041594633589718L;

    private String divCd;
    private String divNm;
    private String divService;
    private String updEnableYn;
    private Integer clientId;
    private String userDefineCol;
}
