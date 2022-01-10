package com.mercury.discovery.base.users.model;

import lombok.Data;

@Data
public class AppUserRequestDto {
    private Integer cmpnyNo;// 필수


    private Integer empNo;  // empNo가 설정되면 grpCd검색은 무시
    
    private String grpCd;   // grpCd가 설정되면 empNo는 무시

    
    //아래 부터는 공통 필터

    private String empNm;   
}