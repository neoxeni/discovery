/**https://nesoy.github.io/articles/2020-02/mysql-datetime-timestamp*/
CREATE TABLE client(
    id                          INT       AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    symbol                      VARCHAR(20)         NULL COMMENT '회사ID',
    name                        VARCHAR(200)    NOT NULL COMMENT '이름',
    eng_name                    VARCHAR(200)        NULL COMMENT '영문명',
    'desc'                      LONGTEXT        NOT NULL COMMENT '비고',
    status                      VARCHAR(10)     NOT NULL COMMENT '상태',
    industry_code               VARCHAR(20)     NOT NULL COMMENT '산업코드',
    created_at                  DATETIME(6)     NOT NULL COMMENT '생성일시',
    updated_at                  DATETIME(6)     NOT NULL COMMENT '수정일시',
    user_id                     INT             NOT NULL COMMENT '생성자',
    client_key                  VARCHAR(36)         NULL COMMENT '회사키(유니크)',
    UNIQUE KEY UK1_CLIENT(client_key)
);

CREATE TABLE client_department(
    id                          BIGINT    AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    parent_department_key       VARCHAR(36)         NULL COMMENT '상위부서키(유니크)',
    department_key              VARCHAR(36)         NULL COMMENT '부서키(유니크)',
    name                        VARCHAR(100)        NULL COMMENT '부서명',
    sort                        INT             NOT NULL DEFAULT 0 COMMENT '정렬순서',
    use_yn                      CHAR(1)         NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by                  INT             NOT NULL COMMENT '수정자',
    updated_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일',

    client_id                   INT             NOT NULL COMMENT '회사 아이디',

    CONSTRAINT FK1_CLIENT_DEPARTMENT FOREIGN KEY (client_id) REFERENCES client (id),
    UNIQUE KEY UK1_CLIENT_DEPARTMENT(department_key),
    INDEX IX1_CLIENT_DEPARTMENT (parent_department_key, sort)
)ENGINE=InnoDB COMMENT='부서';


CREATE TABLE client_user(
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
    status                      VARCHAR(10)         NULL COMMENT '상태',
    created_by                  INT             NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP       NOT NULL COMMENT '생성일',
    updated_by                  INT             NOT NULL COMMENT '수정자',
    updated_at                  TIMESTAMP       NOT NULL COMMENT '수정일',
    user_key                    VARCHAR(36)     NOT NULL COMMENT '유저키(유니크)',

    client_id                   INT                 NULL COMMENT '회사 아이디',
    department_id               BIGINT              NULL COMMENT '부서 아이디',

    CONSTRAINT FK1_CLIENT_USER FOREIGN KEY (client_id) REFERENCES client (id),
    UNIQUE KEY UK1_CLIENT_USER(user_key)
)ENGINE=InnoDB COMMENT='사용자';


CREATE TABLE client_user_login(
    id                          BIGINT    AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    provider_type               VARCHAR(20)     NOT NULL DEFAULT 'LOCAL' COMMENT '프로바이더',
    username                    VARCHAR(100)    NOT NULL COMMENT '사용자아이디',
    password                    VARCHAR(100)    NOT NULL COMMENT '패스워드',
    password_err_count          INT             NOT NULL DEFAULT 0 COMMENT '패스워드오류횟수',
    password_updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '패스워드수정일시',
    last_login_at               TIMESTAMP           NULL COMMENT '최종로그인일시',
    last_logout_at              TIMESTAMP           NULL COMMENT '최종로그아웃일시',
    last_ip_address             VARCHAR(40)         NULL COMMENT '최종접속아이피',
    user_id                     INT             NOT NULL COMMENT '사번',
    CONSTRAINT FK1_CLIENT_USER_LOGIN FOREIGN KEY (user_id) REFERENCES client_user (id),
    UNIQUE UK1_CLIENT_USER_LOGIN(username)
)ENGINE=InnoDB COMMENT='로그인';

CREATE TABLE client_user_login_hist(
    id                          BIGINT     AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    login_at                    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '로그인일시',
    ip_address                  VARCHAR(40)         NULL COMMENT '로그인 IP',
    user_id                     INT                 NULL COMMENT '아이디',
    client_id                   INT                 NULL COMMENT '회사아이디'
)ENGINE=InnoDB COMMENT='로그인이력';


CREATE TABLE cmm_code_div (
    div_cd                      VARCHAR(20)         NOT NULL COMMENT '분류코드',
    div_nm                      VARCHAR(100)        NOT NULL COMMENT '분류명',
    div_service                 VARCHAR(10)             NULL COMMENT '서비스구분',
    upd_enable_yn               CHAR(1)             NOT NULL COMMENT '수정가능여부',
    client_id                   INT                 NOT NULL COMMENT '회사번호',
    user_define_col             VARCHAR(50)             NULL COMMENT '유저정의 컬럼',

    UNIQUE UK1_CMM_CODE_DIV (div_cd,client_id)
)ENGINE=InnoDB COMMENT='코드분류';

CREATE TABLE cmm_code (
    div_cd                      VARCHAR(20)         NOT NULL COMMENT '분류코드',
    code                        VARCHAR(36)         NOT NULL COMMENT '코드',
    parent_code                 VARCHAR(36)         NOT NULL DEFAULT 'ROOT' COMMENT '상위코드',
    name                        VARCHAR(100)        NOT NULL COMMENT '코드명',
    sort                        INT                 NOT NULL DEFAULT 0 COMMENT '정렬번호',
    use_yn                      CHAR(1)             NOT NULL DEFAULT 'Y' COMMENT '사용여부',
    etc1                        VARCHAR(100)            NULL COMMENT 'etc1',
    etc2                        VARCHAR(100)            NULL COMMENT 'etc2',
    etc3                        VARCHAR(100)            NULL COMMENT 'etc3',
    etc4                        VARCHAR(100)            NULL COMMENT 'etc4',
    description                 VARCHAR(200)            NULL COMMENT '설명',
    start_apply_at 			    TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP  COMMENT '적용시작일',
    end_apply_at 			    TIMESTAMP               NULL COMMENT '적용종료일',
    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP           NOT NULL COMMENT '생성일',
    updated_by                  INT                     NULL COMMENT '수정자',
    updated_at                  TIMESTAMP               NULL COMMENT '수정일',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',

    UNIQUE UK1_CMM_CODE (code,div_cd,client_id),
    INDEX IX1_CMM_CODE (div_cd,parent_code,sort),
    INDEX IX2_CMM_CODE (parent_code)
)ENGINE=InnoDB COMMENT='코드';


CREATE TABLE cmm_action_log(
    id                          BIGINT        AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,

    ip                          VARCHAR(40)             NULL COMMENT '접속아이피',
    created_at                  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    menu                        VARCHAR(300)            NULL COMMENT '메뉴명',
    sub_menu                    VARCHAR(300)            NULL COMMENT '서브메뉴명',
    action                      VARCHAR(300)            NULL COMMENT '행위',
    action_url                  VARCHAR(300)            NULL COMMENT 'url',
    input_val                   VARCHAR(4000)           NULL COMMENT '인풋',
    language                    VARCHAR(30)             NULL COMMENT '언어',
    etc1                        VARCHAR(100)            NULL COMMENT '기타1',
    etc2                        VARCHAR(100)            NULL COMMENT '기타2',
    etc3                        VARCHAR(100)            NULL COMMENT '기타3',
    etc4                        VARCHAR(100)            NULL COMMENT '기타4',
    etc5                        VARCHAR(100)            NULL COMMENT '기타5',
    div_cd                      VARCHAR(20)             NULL COMMENT '구분코드',

    user_id                     INT                     NULL COMMENT '사번',
    client_id                   INT                     NULL COMMENT '회사 아이디',
    INDEX IX1_TB_ADMIN_LOG (created_at, client_id)
) ENGINE=InnoDB COMMENT='관리자 로그 히스토리';


CREATE TABLE cmm_group (
    id                          BIGINT         AUTO_INCREMENT COMMENT 'ID' primary key,
    type                        VARCHAR(10)         NOT NULL DEFAULT 'ROLE' COMMENT '타입',
    code                        VARCHAR(36)             NULL COMMENT '코드',
    name                        VARCHAR(1000)           NULL COMMENT '이름',
    use_yn                      CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '사용여부',
    upd_enable_yn               CHAR(1) DEFAULT 'Y' NOT NULL COMMENT '수정가능여부',

    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP           NOT NULL COMMENT '생성일',
    updated_by                  INT                     NULL COMMENT '수정자',
    updated_at                  TIMESTAMP               NULL COMMENT '수정일',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',
    UNIQUE KEY UK1_TB_GROUP(client_id, code)
)ENGINE=InnoDB COMMENT='그룹';

CREATE TABLE cmm_group_mapping(
    id                          BIGINT        AUTO_INCREMENT COMMENT 'ID' primary key,

    target                      VARCHAR(10)         NOT NULL COMMENT 'E:직원,D:부서',
    target_id                   BIGINT              NOT NULL COMMENT '직원,부서번호',
    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    group_id                    BIGINT              NOT NULL COMMENT '그룹번호',
    CONSTRAINT FK1_CMM_GROUP FOREIGN KEY (group_id) REFERENCES cmm_group (id),
    UNIQUE KEY UK1_CMM_GROUP_MAP(group_id, target, target_id)
) ENGINE=InnoDB COMMENT='그룹맵핑';

CREATE TABLE cmm_group_mapping_history(
    id                          BIGINT        AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,

    group_id                    BIGINT              NOT NULL COMMENT '그룹번호',
    group_mapping_id            BIGINT              NOT NULL COMMENT '매핑번호',

    target                      VARCHAR(10)         NOT NULL COMMENT 'E:직원,D:부서',
    target_id                   BIGINT              NOT NULL COMMENT '직원, 부서번호',
    action                      CHAR                NOT NULL COMMENT '액션:CRUD',
    reg_ip                      VARCHAR(100)            NULL COMMENT '등록자IP',

    created_by                  INT                 NOT NULL COMMENT '생성자',
    created_at                  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    client_id                   INT                 NOT NULL COMMENT '회사아이디',
    INDEX IX1_CMM_GROUP_MAP_HST (created_at, client_id)
) ENGINE=InnoDB COMMENT='그룹맵핑 히스토리';

create table if not exists tb_cmm_category(
    cate_id             int           auto_increment comment '카테고리아이디' primary key,
    cate_cd             varchar(20)               null comment '카테고리코드',
    cate_tp             varchar(50)         not null comment '카테고리 유형',
    cate_nm             varchar(200)         null comment '카테고리이름',
    cate_parent_id      int                 not null comment '상위카테고리아이디',
    use_yn              char             DEFAULT 'Y' comment '사용여부',
    cmpny_no            int                     null comment '회사번호',
    sort_no             int                       null comment '순서',
    upd_enable_yn       char             DEFAULT 'N' comment '수정가능여부',
    constraint UK1_TB_CMM_CATEGORY unique (cate_tp, cate_cd, cmpny_no)
) ENGINE=InnoDB COMMENT='카테고리';

create table if not exists tb_cmm_article(
    id            varchar(36)               not null comment '아이디',
    version       int                       not null comment '버전',
    category      int                       not null comment '카테고리',
    cmpny_no      int                       not null comment '회사번호',
    title         varchar(500)              not null comment '제목',
    cntnt         mediumtext                not null comment '내용',
    create_time   datetime                  not null comment '생성일시',
    modify_time   datetime                  not null comment '수정일시',
    create_emp_no int                       not null comment '생성자사번',
    modify_emp_no int                       not null comment '수정자사번',
    del_yn        char                          null comment '삭제여부',
    release_yn    char(1)                       null comment '배포여부',
    primary key (id, version)
) ENGINE=InnoDB COMMENT='매뉴얼';

create table if not exists tb_cmm_category_ext(
    cate_id             int                     not null primary key comment '카테고리아이디',
    INT_1               int                     null,
    INT_2               int                     null,
    INT_3               int                     null,
    INT_4               int                     null,
    INT_5               int                     null,
    INT_6               int                     null,
    INT_7               int                     null,
    INT_8               int                     null,
    INT_9               int                     null,
    INT_10              int                     null,
    BIGINT_1            bigint                  null,
    BIGINT_2            bigint                  null,
    BIGINT_3            bigint                  null,
    BIGINT_4            bigint                  null,
    BIGINT_5            bigint                  null,
    BIGINT_6            bigint                  null,
    BIGINT_7            bigint                  null,
    BIGINT_8            bigint                  null,
    BIGINT_9            bigint                  null,
    BIGINT_10           bigint                  null,
    VARCHAR_1           varchar(200)            null,
    VARCHAR_2           varchar(200)            null,
    VARCHAR_3           varchar(200)            null,
    VARCHAR_4       varchar(200)            null,
    VARCHAR_5       varchar(200)            null,
    VARCHAR_6       varchar(200)            null,
    VARCHAR_7       varchar(200)            null,
    VARCHAR_8       varchar(200)            null,
    VARCHAR_9       varchar(200)            null,
    VARCHAR_10      varchar(200)            null,
    TEXT_1       TEXT           null,
    TEXT_2       TEXT           null,
    TEXT_3       TEXT        null,
    TEXT_4       TEXT       null,
    TEXT_5       TEXT    null,
    TEXT_6       TEXT    null,
    TEXT_7       TEXT         null,
    TEXT_8       TEXT   null,
    TEXT_9       TEXT        null,
    TEXT_10      TEXT         null
) ENGINE=InnoDB COMMENT='카테고리 확장테이블';