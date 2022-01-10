package com.mercury.discovery.base.company.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * tb_cmm_company
 */

@Alias("Company")
@Data
public class Company implements Serializable {

    private static final long serialVersionUID = -621627670582569218L;
    private Integer cmpnyNo;
    private String cmpnyNm;
    private String cmpnyId;
    private String telNo;
    private String ceoNm;
    private String domain;      //도메인
    private String domainUseYn; //도메인사용여부
    private String addr;        //주소
    private String bizNo;       //사업자등록번호

    private String callTelNo;   //콜센터_대표번호
    private String smsTelNo;    //SMS 전송 전화번호
    private String email;       //대표 메일주소
    private String emailPw;     //메일패스워드
    private String smtpHost;    //SMTP 호스트
    private Integer smtpPort;   //SMTP 포트
    private String smtpSslYn;   //SMTP 보안연결 여부

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private CompanyStatus status;
    private Integer holdingsNo;
    private String cntnt;


    //이메일 서비스가 설정되어 있는지
    public boolean isSetEmailInfo() {
        return email != null && emailPw != null && smtpHost != null && smtpPort != null;
    }

    //SMS 서비스가 설정되어 있는지
    public boolean isSetSmsInfo() {
        return smsTelNo != null;
    }
}