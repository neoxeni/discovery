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

    client_id                   INT             NOT NULL COMMENT '회사 아이디',

    CONSTRAINT FK1_CLIENT_CLIENT_ID FOREIGN KEY (client_id) REFERENCES client (id),
    UNIQUE KEY UK1_CLIENT_DEPARTMENT_KEY(department_key),
    INDEX IX1_CLIENT_DEPARTMENT (parent_id, sort)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='부서';


create table client_user(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    name                        VARCHAR(100)    NOT NULL COMMENT '이름',
    nickname                    VARCHAR(100)    NOT NULL COMMENT '별명',
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
    updated_by                  INT             NOT NULL COMMENT '수정자',
    updated_at                  DATETIME        NOT NULL COMMENT '수정일',
    user_key                    VARCHAR(36)     NOT NULL COMMENT '유저키(유니크)',

    client_id                   INT                 NULL COMMENT '회사 아이디',
    department_id               INT                 NULL COMMENT '부서 아이디',

    CONSTRAINT FK1_CLIENT_CLIENT_ID FOREIGN KEY (client_id) REFERENCES client (id),
    CONSTRAINT FK2_DEPARTMENT_DEPARTMENT_ID FOREIGN KEY (department_id) REFERENCES client_department (id),
    UNIQUE KEY UK1_CLIENT_USER_KEY(user_key)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자';


create table client_user_login(
    id                          INT       auto_increment COMMENT 'ID' PRIMARY KEY,
    username                    VARCHAR(100)    NOT NULL COMMENT '사용자아이디'
    password                    VARCHAR(100)    NOT NULL comment '패스워드',
    password_err_count          INT             NOT NULL default 0 comment '패스워드오류횟수',
    password_updated_at         DATETIME        NOT NULL default CURRENT_TIMESTAMP comment '패스워드수정일시',
    last_login_at               DATETIME            NULL comment '최종로그인일시',
    last_logout_at              DATETIME            NULL comment '최종로그아웃일시',
    last_ip_address             VARCHAR(40)         NULL comment '최종접속아이피',

    user_id                     INT             NOT NULL comment '사번',
    CONSTRAINT FK1_CLIENT_USER_USER_ID FOREIGN KEY (user_id) REFERENCES client_user (id),
    UNIQUE UK1_CLIENT_USER_LOGIN(username)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인';

create table client_user_login_hist(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    user_id                     INT                 null comment '아이디',
    login_at                    datetime            null comment '로그인일시',
    last_ip_address             varchar(40)         null comment '로그인IP',
    client_id                   int                 null comment '회사아이디'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인이력';


CREATE TABLE cmm_code_div (
    div_cd              varchar(20)         NOT NULL COMMENT '분류코드',
    div_nm              varchar(100)        NOT NULL COMMENT '분류명',
    div_service         varchar(10)             NULL COMMENT '서비스구분',
    upd_enable_yn       char(1)             NOT NULL COMMENT '수정가능여부',
    client_id           int(11)             NOT NULL COMMENT '회사번호',
    user_define_col     varchar(50)             NULL COMMENT '유저정의 컬럼',

    UNIQUE UK1_CMM_CODE_DIV (div_cd,client_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드분류';

CREATE TABLE cmm_code (
    cd                  varchar(36)         NOT NULL COMMENT '코드',
    div_cd              varchar(20)         NOT NULL COMMENT '분류코드',
    prnt_cd             varchar(36)         DEFAULT '0' COMMENT '상위코드',
    cd_nm               varchar(100)        NOT NULL COMMENT '코드명',
    sort_no             int(11)             DEFAULT 0 COMMENT '정렬번호',
    dtl                 varchar(200)            NULL COMMENT '코드상세',
    use_yn              char(1)             DEFAULT 'Y' COMMENT '사용여부',

    etc1                varchar(100)            NULL COMMENT 'etc1',
    etc2                varchar(100)            NULL COMMENT 'etc2',
    etc3                varchar(100)            NULL COMMENT 'etc3',
    etc4                varchar(100)            NULL COMMENT 'etc4',

    start_apply_at 			    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '적용시작일',
    end_apply_at 			    DATETIME        NOT NULL DEFAULT '2099-12-31T23:59:59.999'  COMMENT '적용종료일',
    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  DATETIME        NOT NULL COMMENT '생성일',
    updated_by                  INT                 NULL COMMENT '수정자',
    updated_at                  DATETIME            NULL COMMENT '수정일',
    client_id                   INT             NOT NULL comment '회사아이디',

    UNIQUE UK1_CMM_CODE (cd,div_cd,client_id),
    INDEX IX1_CMM_CODE (div_cd,prnt_cd,sort_no),
    INDEX IX2_CMM_CODE (prnt_cd)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드';





create table cmm_action_log(
    id                  bigint        auto_increment comment 'ID' primary key,
    user_id             int                     null comment '사번',
    ip                  varchar(40)             null comment '접속아이피',
    created_at          datetime default current_timestamp() comment '등록일시',
    menu                varchar(300)            null comment '메뉴명',
    sub_menu            varchar(300)            null comment '서브메뉴명',
    action              varchar(300)            null comment '행위',
    action_url          varchar(300)            null comment 'url',
    input_val           varchar(4000)           null comment '인풋',
    language            varchar(30)             null comment '언어',
    etc1                varchar(100)            null comment '기타1',
    etc2                varchar(100)            null comment '기타2',
    etc3                varchar(100)            null comment '기타3',
    etc4                varchar(100)            null comment '기타4',
    etc5                varchar(100)            null comment '기타5',
    div_cd              varchar(20)             null comment '구분코드',
    client_id                   INT             NULL COMMENT '회사 아이디',
    INDEX IX1_TB_ADMIN_LOG (created_at, client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='관리자 로그 히스토리';




