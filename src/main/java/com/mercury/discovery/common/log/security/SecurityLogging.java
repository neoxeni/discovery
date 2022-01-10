package com.mercury.discovery.common.log.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecurityLogging {
    String menu() default    "menu";//menu인경우 sitemenu의 menu객체에서 추출, 다른값을 경우 해당 값으로 입력
    String subMenu() default "";    //menu를 구분할 수 있는 추가 subMenu값

    String etc1() default    "";
    String etc2() default    "";
    String etc3() default    "";
    String etc4() default    "";
    String etc5() default    "";
    String divCd() default   "ADMIN"; //ADMIN: 관리자, USER: 사용자, SECU: 개인정보
}
