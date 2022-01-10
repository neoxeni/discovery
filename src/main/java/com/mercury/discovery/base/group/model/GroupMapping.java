package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

/**
 * tb_cmm_group_map
 */

@Alias("GroupMapping")
@Data
@EqualsAndHashCode(of = {"grpNo", "dataGbn", "dataNo"})
public class GroupMapping {
    private Integer mapNo;
    private Integer grpNo;
    private String dataGbn;
    private Integer dataNo;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private Integer regEmpNo;

    private String useYn;
    private Integer sortNo;
}
