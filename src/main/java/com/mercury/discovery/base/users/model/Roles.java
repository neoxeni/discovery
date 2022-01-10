package com.mercury.discovery.base.users.model;

public enum Roles {
     //Roles에 정의되는 ENUM 값은 updEnableYn 값이 N 인 값들
     SYS_ADMIN("시스템 관리자"),
     CNSLT_ADMIN("상담 관리자"),
     ;

     private String label;

     Roles(String label){
          this.label = label;
     }

     public String getLabel(){
          return this.label;
     };

     public static boolean isSysAdmin(AppUser appUser){ return appUser.hasAnyRole(SYS_ADMIN.name()); }

}
