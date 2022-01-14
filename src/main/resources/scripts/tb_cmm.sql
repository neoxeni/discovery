create table client_department(
    id                          BIGINT     AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    parent_department_key       VARCHAR(36)         NULL COMMENT '상위부서키(유니크)',
    department_key              VARCHAR(36)         NULL COMMENT '부서키(유니크)',
    name                        VARCHAR(100)        NULL COMMENT '부서명',
    sort                        INT             NOT NULL DEFAULT 0 COMMENT '정렬순서',
    use_yn                      CHAR(1)             NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  DATETIME        NOT NULL default CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by                  INT             NOT NULL COMMENT '수정자',
    updated_at                  DATETIME        NOT NULL default CURRENT_TIMESTAMP COMMENT '수정일',

    client_id                   INT             NOT NULL COMMENT '회사 아이디',

    CONSTRAINT FK1_CLIENT_CLIENT_ID FOREIGN KEY (client_id) REFERENCES client (id),
    UNIQUE KEY UK1_CLIENT_DEPARTMENT_KEY(department_key),
    INDEX IX1_CLIENT_DEPARTMENT (parent_department_key, sort)
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
    provider_type               VARCHAR(20)     NOT NULL DEFAULT 'LOCAL' COMMENT '로그인 프로바이더',
    username                    VARCHAR(100)    NOT NULL COMMENT '사용자아이디'
    password                    VARCHAR(100)    NOT NULL COMMENT '패스워드',
    password_err_count          INT             NOT NULL default 0 COMMENT '패스워드오류횟수',
    password_updated_at         DATETIME        NOT NULL default CURRENT_TIMESTAMP COMMENT '패스워드수정일시',
    last_login_at               DATETIME            NULL COMMENT '최종로그인일시',
    last_logout_at              DATETIME            NULL COMMENT '최종로그아웃일시',
    last_ip_address             VARCHAR(40)         NULL COMMENT '최종접속아이피',
    user_id                     INT             NOT NULL COMMENT '사번',
    CONSTRAINT FK1_CLIENT_USER_USER_ID FOREIGN KEY (user_id) REFERENCES client_user (id),
    UNIQUE UK1_CLIENT_USER_LOGIN(username)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인';

create table client_user_login_hist(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    user_id                     INT                 NULL COMMENT '아이디',
    login_at                    DATETIME            NULL COMMENT '로그인일시',
    last_ip_address             VARCHAR(40)         NULL COMMENT '로그인IP',
    client_id                   INT                 NULL COMMENT '회사아이디'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인이력';


CREATE TABLE cmm_code_div (
    div_cd                      VARCHAR(20)         NOT NULL COMMENT '분류코드',
    div_nm                      VARCHAR(100)        NOT NULL COMMENT '분류명',
    div_service                 VARCHAR(10)             NULL COMMENT '서비스구분',
    upd_enable_yn               CHAR(1)             NOT NULL COMMENT '수정가능여부',
    client_id                   INT                 NOT NULL COMMENT '회사번호',
    user_define_col             VARCHAR(50)             NULL COMMENT '유저정의 컬럼',

    UNIQUE UK1_CMM_CODE_DIV (div_cd,client_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드분류';

CREATE TABLE cmm_code (
    div_cd                      VARCHAR(20)         NOT NULL COMMENT '분류코드',
    cd                          VARCHAR(36)         NOT NULL COMMENT '코드',
    parent_code                 VARCHAR(36)         NOT NULL DEFAULT 'ROOT' COMMENT '상위코드',
    name                        VARCHAR(100)        NOT NULL COMMENT '코드명',
    sort                        INT                 NOT NULL DEFAULT 0 COMMENT '정렬번호',
    use_yn                      CHAR(1)             NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    etc1                        VARCHAR(100)            NULL COMMENT 'etc1',
    etc2                        VARCHAR(100)            NULL COMMENT 'etc2',
    etc3                        VARCHAR(100)            NULL COMMENT 'etc3',
    etc4                        VARCHAR(100)            NULL COMMENT 'etc4',
    description                 VARCHAR(200)            NULL COMMENT '설명',
    start_apply_at 			    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '적용시작일',
    end_apply_at 			    DATETIME            NOT NULL DEFAULT '2099-12-31T23:59:59.999'  COMMENT '적용종료일',
    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  DATETIME            NOT NULL COMMENT '생성일',
    updated_by                  INT                     NULL COMMENT '수정자',
    updated_at                  DATETIME                NULL COMMENT '수정일',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',

    UNIQUE UK1_CMM_CODE (cd,div_cd,client_id),
    INDEX IX1_CMM_CODE (div_cd,parent_code,sort),
    INDEX IX2_CMM_CODE (parent_code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드';


CREATE TABLE cmm_action_log(
    id                  BIGINT        AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,

    ip                  VARCHAR(40)             NULL COMMENT '접속아이피',
    created_at          DATETIME default current_timestamp() COMMENT '등록일시',
    menu                VARCHAR(300)            NULL COMMENT '메뉴명',
    sub_menu            VARCHAR(300)            NULL COMMENT '서브메뉴명',
    action              VARCHAR(300)            NULL COMMENT '행위',
    action_url          VARCHAR(300)            NULL COMMENT 'url',
    input_val           VARCHAR(4000)           NULL COMMENT '인풋',
    language            VARCHAR(30)             NULL COMMENT '언어',
    etc1                VARCHAR(100)            NULL COMMENT '기타1',
    etc2                VARCHAR(100)            NULL COMMENT '기타2',
    etc3                VARCHAR(100)            NULL COMMENT '기타3',
    etc4                VARCHAR(100)            NULL COMMENT '기타4',
    etc5                VARCHAR(100)            NULL COMMENT '기타5',
    div_cd              VARCHAR(20)             NULL COMMENT '구분코드',

    user_id             INT                     NULL COMMENT '사번',
    client_id           INT                     NULL COMMENT '회사 아이디',
    INDEX IX1_TB_ADMIN_LOG (created_at, client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='관리자 로그 히스토리';


create table cmm_group (
    id                          bigint         auto_increment comment 'ID' primary key,
    type                        varchar(10)         not null default 'ROLE' comment '타입',
    code                        varchar(36)             null comment '코드',
    name                        varchar(1000)           null comment '이름',
    use_yn                      char(1) default 'Y' not null comment '사용여부',
    upd_enable_yn               char(1) default 'Y' not null comment '수정가능여부',

    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  DATETIME            NOT NULL COMMENT '생성일',
    updated_by                  INT                     NULL COMMENT '수정자',
    updated_at                  DATETIME                NULL COMMENT '수정일',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',
    UNIQUE KEY UK1_TB_GROUP(client_id, code)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹';

create table cmm_group_mapping(
    id                          bigint        auto_increment comment 'ID' primary key,

    target                      varchar(10)         not null comment 'E:직원,D:부서',
    target_id                   bigint              not null comment '직원,부서번호',
    use_yn                      char(1) default 'Y' not null comment '사용여부',
    sort                        int     default 0   not null comment '정렬순서',
    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  DATETIME            default current_timestamp COMMENT '등록일시',
    group_id                    bigint              not null comment '그룹번호',
    CONSTRAINT FK1_CMM_GROUP FOREIGN KEY (group_id) REFERENCES cmm_group (id),
    UNIQUE KEY UK1_CMM_GROUP_MAP(group_id, target, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹맵핑';

create table cmm_group_mapping_history(
    id                          bigint         auto_increment comment 'ID' primary key,

    group_id                    bigint                 not null comment '그룹번호',
    group_mapping_id            int                 not null comment '매핑번호',

    target                      varchar(10)         not null comment 'E:직원,D:부서',
    target_id                   bigint              not null comment '직원,부서번호',
    action                      char                not null comment '액션CRUD',
    reg_ip                      varchar(100)            null comment '등록자IP',

    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  DATETIME            default current_timestamp COMMENT '등록일시',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',
    INDEX IX1_cmm_GROUP_MAP_HST (created_at, client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹맵핑 히스토리';

