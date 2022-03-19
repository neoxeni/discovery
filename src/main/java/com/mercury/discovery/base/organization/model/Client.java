package com.mercury.discovery.base.organization.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Alias("Client")
@Data
public class Client implements Serializable {
    private static final long serialVersionUID = -1784321314856026719L;

    private Integer id;
    private String symbol;
    private String name;
    private String engName;
    private String desc;
    private String status;
    private String industryCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer userId;
    private String clientKey;
}
