create table client_department(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    parent_id                   INT             NOT NULL DEFAULT 0 COMMENT '상위부서 아이디',
    name                        VARCHAR(100)        NULL COMMENT '부서명',
    sort                        INT             NOT NULL DEFAULT 0 COMMENT '정렬순서',

    is_use                      TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '사용여부',
    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  DATETIME        NOT NULL COMMENT '생성일',
    received_by                 INT             NOT NULL COMMENT '수정자',
    received_at                 DATETIME        NOT NULL COMMENT '수정일',
    department_key              VARCHAR(36)         NULL COMMENT '부서키(유니크)',

    client_id                   INT                 NULL COMMENT '회사 아이디',

    CONSTRAINT FK1_CLIENT_CLIENT_ID FOREIGN KEY (client_id) REFERENCES client (id),
    UNIQUE KEY UK1_CLIENT_DEPARTMENT_KEY(department_key),
    INDEX IX1_CLIENT_DEPARTMENT (parent_id, sort)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='부서';


create table client_user(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    name                        VARCHAR(100)        NULL COMMENT '이름',
    nickname                    VARCHAR(100)        NULL COMMENT '예명',
    phone                       VARCHAR(15)         NULL COMMENT '전화번호',
    email                       VARCHAR(50)         NULL COMMENT '이메일',

    identification              VARCHAR(20)         NULL COMMENT '사번',
    extension_no                VARCHAR(20)         NULL COMMENT '내선번호',
    position_cd                 VARCHAR(36)         NULL COMMENT '직위코드',
    duty_cd                     VARCHAR(36)         NULL COMMENT '직책코드',
    sort                        INT             NOT NULL DEFAULT 0 COMMENT '표시순서',
    status                      VARCHAR(10)         NULL COMMENT '사원상태',
    join_date                   DATE            NOT NULL COMMENT '입사일',
    is_retire                   TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '퇴사여부',
    retire_date                 DATE                NULL COMMENT '퇴사일',

    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  DATETIME        NOT NULL COMMENT '생성일',
    received_by                 INT             NOT NULL COMMENT '수정자',
    received_at                 DATETIME        NOT NULL COMMENT '수정일',
    user_key                    VARCHAR(36)     NOT NULL COMMENT '유저키(유니크)',

    client_id                   INT                 NULL COMMENT '회사 아이디',
    department_id               INT                 NULL COMMENT '부서 아이디',

    CONSTRAINT FK1_CLIENT_CLIENT_ID FOREIGN KEY (client_id) REFERENCES client (id),
    CONSTRAINT FK2_DEPARTMENT_DEPARTMENT_ID FOREIGN KEY (department_id) REFERENCES client_department (id),
    UNIQUE KEY UK1_CLIENT_USER_KEY(user_key),
    INDEX IX1_TB_CMM_EMP (dept_no),
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자';


create table client_user_login(
    id                          INT       auto_increment COMMENT 'ID' PRIMARY KEY,
    username                    varchar(100)    NOT NULL COMMENT '사용자 아이디'
    password                    varchar(100)    NOT NULL comment '패스워드',
    password_err_count          int             NOT NULL default 0 comment '패스워드오류횟수',
    password_update_at          datetime        NOT NULL default CURRENT_TIMESTAMP comment '패스워드수정일시',
    last_login_at               datetime            NULL comment '최종로그인일시',
    last_logout_at              datetime            NULL comment '최종로그아웃일시',
    last_ip_address             varchar(40)         NULL comment '최종접속아이피',

    user_id                     int             NOT NULL comment '사번',
    CONSTRAINT FK1_CLIENT_USER_USER_ID FOREIGN KEY (user_id) REFERENCES client_user (id),
    UNIQUE UK1_CLIENT_USER_LOGIN(username)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인';


CREATE TABLE tb_cmm_code_div (
    div_cd                      VARCHAR(36)     NOT NULL COMMENT '분류코드',
    div_nm                      VARCHAR(100)    NOT NULL COMMENT '분류명',
    div_service                 VARCHAR(10)         NULL COMMENT '서비스구분',
    upd_enable_yn               char(1)         NOT NULL COMMENT '수정가능여부',
    cmpny_no                    INT(11)         NOT NULL COMMENT '회사번호'
    PRIMARY KEY (div_cd,cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드분류';


CREATE TABLE tb_cmm_code (
    cd                          VARCHAR(36)     NOT NULL COMMENT '코드',
    div_cd                      VARCHAR(20)     NOT NULL COMMENT '분류코드',
    prnt_cd                     VARCHAR(36)     NOT NULL DEFAULT '0' COMMENT '상위코드',
    cd_nm                       VARCHAR(100)    NOT NULL COMMENT '코드명',
    sort_no                     INT(11)         NOT NULL DEFAULT 0 COMMENT '정렬번호',
    dtl                         VARCHAR(200)        NULL COMMENT '코드상세',
    use_yn                      char(1)         NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    reg_emp_no                  INT(11)         NOT NULL COMMENT '등록자',
    reg_dt                      datetime        NOT NULL COMMENT '등록일시',
    upd_emp_no                  INT(11)             NULL COMMENT '수정자',
    upd_dt                      datetime            NULL COMMENT '수정일시',
    etc1                        VARCHAR(100)        NULL COMMENT 'etc1',
    etc2                        VARCHAR(100)        NULL COMMENT 'etc2',
    etc3                        VARCHAR(100)        NULL COMMENT 'etc3',
    etc4                        VARCHAR(100)        NULL COMMENT 'etc4',
    cmpny_no                    INT(11)         NOT NULL COMMENT '회사번호',
    lvl                         INT(11)             NULL COMMENT '레벨',
    adpt_fr_dt 			        VARCHAR(8)      NOT NULL DEFAULT DATE_FORMAT(now(),'%Y%m%d')  COMMENT '적용시작일',
    adpt_to_dt 			        VARCHAR(8)      NOT NULL DEFAULT '20991231'  COMMENT '적용종료일',
    PRIMARY KEY (cd,div_cd,cmpny_no),
    INDEX IX1_TB_CODE (div_cd,prnt_cd,sort_no),
    INDEX IX2_TB_CODE (prnt_cd)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드';


