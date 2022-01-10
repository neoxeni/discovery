create table tb_cmm_emp(
    emp_no              int       auto_increment comment '사번' primary key,
    cmpny_emp_cd        varchar(20)         null comment '기업사원코드',
    dept_no             int                 null comment '부서번호',
    emp_nm              varchar(100)        null comment '이름',
    ext_tel_no          varchar(100)        null comment '내선번호',
    hire_dd             varchar(8)          null comment '입사일',
    rtrmnt_yn           char            not null default 'N' comment '퇴사여부',
    rtrmnt_dd           varchar(8)          null comment '퇴사일',
    reg_emp_no          int                 null comment '등록자사번',
    reg_dt              datetime            null comment '등록일자',
    upd_emp_no          int                 null comment '수정자사번',
    upd_dt              datetime            null comment '수정일자',

    postn_cd            varchar(36)         null comment '직위코드',
    duty_cd             varchar(36)         null comment '직책',
    cmpny_no            int                 null comment '회사번호',
    emp_sort            int                 null comment '직원표시순서',

    email               varchar(50)         null comment '이메일',
    emp_tp              varchar(2)          null comment '사원구분',
    real_emp_nm         varchar(100)        null comment '실명',
    user_key            varchar(36)     not null comment '유저키(유니크)',
    UNIQUE KEY UK1_TB_CMM_EMP(user_key),
    INDEX IX1_TB_CMM_EMP (dept_no),
    INDEX IX2_TB_CMM_EMP (cmpny_no, rtrmnt_yn, emp_nm),
    index IX3_TB_CMM_EMP (cmpny_emp_cd, emp_no, emp_nm)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사원';