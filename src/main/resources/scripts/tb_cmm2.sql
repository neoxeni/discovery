DROP TABLE  IF EXISTS tb_cmm_admin_log;
DROP TABLE  IF EXISTS tb_cmm_app_group;
DROP TABLE  IF EXISTS tb_cmm_app_group_map;
DROP TABLE  IF EXISTS tb_cmm_article;
DROP TABLE  IF EXISTS tb_cmm_attach_file;

DROP TABLE  IF EXISTS tb_cmm_code;
DROP TABLE  IF EXISTS tb_cmm_code_div;
DROP TABLE  IF EXISTS tb_cmm_company;

DROP TABLE  IF EXISTS tb_cmm_dept;
DROP TABLE  IF EXISTS tb_cmm_emp;

DROP TABLE  IF EXISTS tb_cmm_group;
DROP TABLE  IF EXISTS tb_cmm_group_map;
DROP TABLE  IF EXISTS tb_cmm_group_map_hst;

DROP TABLE  IF EXISTS tb_cmm_login;
DROP TABLE  IF EXISTS tb_cmm_login_hist;
DROP TABLE  IF EXISTS tb_cmm_logout_hist;

DROP TABLE  IF EXISTS tb_cmm_sitemenu;



create table tb_cmm_admin_log(
    seq_no              int           auto_increment comment '시퀀스' primary key,
    emp_no              int                     null comment '사번',
    ip                  varchar(15)             null comment '접속아이피',
    reg_dt              datetime default current_timestamp() comment '등록일시',
    menu                varchar(300)            null comment '메뉴명',
    sub_menu            varchar(300)            null comment '서브메뉴명',
    action              varchar(300)            null comment '행위',
    action_url          varchar(300)            null comment 'url',
    input_val           varchar(4000)           null comment '인풋',
    reg_nation          varchar(30)             null comment '국가코드',
    etc1                varchar(100)            null comment '기타1',
    etc2                varchar(100)            null comment '기타2',
    etc3                varchar(100)            null comment '기타3',
    etc4                varchar(100)            null comment '기타4',
    etc5                varchar(100)            null comment '기타5',
    div_cd              varchar(20)             null comment '구분코드',
    cmpny_no            int                 not null comment '회사번호',
    INDEX IX1_TB_ADMIN_LOG (reg_dt, emp_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='관리자 로그 히스토리';

create table tb_cmm_app_group (
    app_grp_no   int auto_increment primary key comment '어플리케이션그룹번호',
    cate_id int    not null comment '어플리케이션그룹카테고리',
    app_grp_cd  varchar(20)   not null comment '어플리케이션그룹코드',
    app_grp_nm  varchar(1000) not null comment '어플리케이션그룹명',
    reg_dt      datetime      not null comment '등록일',
    upd_dt      datetime      null comment '수정일',
    reg_user_no int           not null comment '등록자사번',
    upd_user_no int           null comment '변경자사번',
    cmpny_no    int           not null comment '회사번호',
    UNIQUE KEY UK1_tb_cmm_app_group(app_grp_cd, cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='어플리케이션그룹';

create table tb_cmm_app_group_map(
    map_no              bigint        auto_increment comment '시퀀스' primary key,
    app_grp_no          int                 not null comment '그룹번호',
    data_gbn            char(1)             not null comment 'E:직원,D:부서',
    data_no             int                 not null comment '직원,부서번호',
    reg_emp_no          int                 not null comment '등록자',
    reg_dt              datetime default current_timestamp() comment '등록일시',
    use_yn              char(1) default 'Y' not null comment '사용여부',
    sort_no             int     default 0   not null comment '정렬순서',
    UNIQUE KEY UK1_tb_cmm_app_group_MAP(app_grp_no, data_gbn, data_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='어플리케이션그룹맵핑';


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='매뉴얼';

CREATE TABLE tb_cmm_attach_file (
    file_no         int(11)           AUTO_INCREMENT COMMENT '파일번호',
    file_nm         varchar(200)                NULL COMMENT '파일명',
    file_path       varchar(200)                NULL COMMENT '파일경로',
    file_size       int(11)                     NULL COMMENT '파일사이즈',
    ext_nm          varchar(10)                 NULL COMMENT '확장자',
    reg_dt          datetime                    NULL COMMENT '등록일시',
    user_file_nm    varchar(1000)               NULL COMMENT '사용자파일명',
    upd_dt          datetime                    NULL COMMENT '수정일시',
    data_no         varchar(30)                 NULL COMMENT 'data_no',
    attach_div_cd   varchar(30)                 NULL COMMENT '첨부분류코드',
    note_no         int(11)                     NULL COMMENT '쪽지번호',
    meta            VARCHAR(4000)       DEFAULT '{}' COMMENT '파일메타',
    cmpny_no        int(11)                     NULL COMMENT '회사번호',
	file_key        varchar(36)         DEFAULT '-'  COMMENT '파일키',
    PRIMARY KEY (file_no),
    INDEX IX1_TB_CMM_ATTACH_FILE (attach_div_cd,data_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='첨부파일';


CREATE TABLE tb_cmm_batch_log (
    seq_no         int(11)                 not null COMMENT '일련번호',
    reg_dt         datetime                NULL COMMENT '등록일시',
    batch_id       varchar(20)             NULL COMMENT '배치아이디',
    batch_log       varchar(1000)          NULL COMMENT '배치로그',
    PRIMARY KEY (seq_no),
    INDEX IX1_TB_CMM_BATCH_LOG (reg_dt)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='배치로그';

CREATE TABLE tb_cmm_calendar
(
    seq_no     INT 				AUTO_INCREMENT  COMMENT '일련번호',
    cmpny_no   INT              NOT NULL COMMENT '회사번호',
    reg_emp_no INT              NOT NULL COMMENT '등록자사번',
    schdl_type VARCHAR(20)      NOT NULL COMMENT '일정유형',
    schdl_nm   VARCHAR(1000)    NOT NULL COMMENT '일정명',
    reg_dt     datetime         NOT NULL COMMENT '등록일',
    start_dt   datetime         NULL COMMENT '일정시작일',
    end_dt     datetime         NULL COMMENT '일정종료일',
    upd_emp_no INT              NULL COMMENT '수정자사번',
    upd_dt     datetime         NULL COMMENT '수정일',
    is_allday  char default 'N' NOT NULL COMMENT '종일여부',
    PRIMARY KEY (seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='일정';

CREATE TABLE tb_cmm_calendar_shared
(
    calendar_seq_no INT         NOT NULL COMMENT '일정번호',
    shared_type     VARCHAR(20) NOT NULL COMMENT '공유타입(개인,부서,그룹,전체)',
    shared_no       INT         NOT NULL COMMENT '공유변호(개인사번,부서번호,그룹번호,전체(-1))',
    reg_dt          datetime    NOT NULL COMMENT '공유등록날짜',
    PRIMARY KEY (calendar_seq_no, shared_type, shared_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='공유일정';

create table if not exists tb_cmm_category(
    cate_id             int           auto_increment comment '카테고리아이디' primary key,
    cate_cd             varchar(20)               null comment '카테고리코드',
    cate_tp             varchar(50)         not null comment '카테고리 유형',
    cate_nm             varchar(200)         null comment '카테고리이름',
    cate_parent_id      int                 not null comment '상위카테고리아이디',
    use_yn              char             DEFAULT 'Y' comment '사용여부',
    cmpny_no            int                     null comment '회사번호',
    sort_no           int                       null comment '순서',
	upd_enable_yn       char             DEFAULT 'N' comment '수정가능여부',
	constraint tb_cmm_category_cate_cd_uindex
        unique (cate_tp, cate_cd, cmpny_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='카테고리';

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
	VARCHAR_1       varchar(200)            null,
	VARCHAR_2       varchar(200)            null,
	VARCHAR_3       varchar(200)            null,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='카테고리 확장테이블';



create table if not exists tb_cmm_cnslt_tool(
    seq_no          int               auto_increment comment '시퀀스',
    div_cd          varchar(20)                 null comment '상담구분코드',
    cnslt_div_cd    varchar(36)                 null comment '상담분류코드',
    title           varchar(200)                null comment '제목',
    content         varchar(2000)               null comment '내용',
    description     varchar(2000)               null comment '메모',
    use_yn          char                        null comment '사용여부',
    reg_emp_no      int                         null comment '등록자',
    reg_dt          datetime                    null comment '등록일시',
    upd_emp_no      int                         null comment '수정자',
    upd_dt          datetime                    null comment '수정일시',
    cmpny_no        int                         null comment '회사번호',
    PRIMARY KEY (seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상담도구';


CREATE TABLE tb_cmm_code (
    cd                  varchar(36)         NOT NULL COMMENT '코드',
    div_cd              varchar(20)         NOT NULL COMMENT '분류코드',
    prnt_cd             varchar(36)         DEFAULT '0' COMMENT '상위코드',
    name               varchar(100)        NOT NULL COMMENT '코드명',
    sort_no             int(11)             DEFAULT 0 COMMENT '정렬번호',
    dtl                 varchar(200)            NULL COMMENT '코드상세',
    use_yn              char(1)             DEFAULT 'Y' COMMENT '사용여부',
    reg_emp_no          int(11)             NOT NULL COMMENT '등록자',
    reg_dt              datetime default current_timestamp() COMMENT '등록일시',
    upd_emp_no          int(11)                 NULL COMMENT '수정자',
    upd_dt              datetime                NULL COMMENT '수정일시',
    etc1                varchar(100)            NULL COMMENT 'etc1',
    etc2                varchar(100)            NULL COMMENT 'etc2',
    etc3                varchar(100)            NULL COMMENT 'etc3',
    etc4                varchar(100)            NULL COMMENT 'etc4',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    lvl                 int(11)                 NULL COMMENT '레벨',
	adpt_fr_dt 			varchar(8)             DEFAULT DATE_FORMAT(now(),'%Y%m%d') NOT NULL COMMENT '적용시작일',
	adpt_to_dt 			varchar(8)             DEFAULT '20991231' NOT NULL COMMENT '적용종료일',
    PRIMARY KEY (cd,div_cd,cmpny_no),
    INDEX IX1_TB_CODE (div_cd,prnt_cd,sort_no),
    INDEX IX2_TB_CODE (prnt_cd)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드';


CREATE TABLE tb_cmm_code_div (
    div_cd              varchar(20)         NOT NULL COMMENT '분류코드',
    div_nm              varchar(100)        NOT NULL COMMENT '분류명',
    div_service         varchar(10)             NULL COMMENT '서비스구분',
    upd_enable_yn       char(1)             NOT NULL COMMENT '수정가능여부',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    user_define_col     varchar(50)             NULL COMMENT '유저정의 컬럼',

    PRIMARY KEY (div_cd,cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='코드분류';


CREATE TABLE tb_cmm_company (
    cmpny_no            int           auto_increment comment '회사번호' primary key,
    cmpny_nm            varchar(100)            null comment '회사명',
    cmpny_id            varchar(20)         not null comment '회사ID',
    tel_no              varchar(20)             null comment '전화번호',
    ceo_nm              varchar(100)            null comment '대표자명',
    domain              varchar(100)            null comment '도메인',
    addr                varchar(1000)           null comment '주소',

    call_tel_no         varchar(20)             null comment '콜센터_대표번호',
    sms_tel_no          varchar(20)             null comment 'SMS 전화번호',
    email               varchar(50)             null comment '메일주소',
    email_pw            varchar(100)            null comment '메일패스워드',
    smtp_host           varchar(50)             NULL comment 'SMTP 호스트',
    smtp_port           int                     NULL comment 'SMTP 포트',
    smtp_ssl_yn         char(1)                 NULL DEFAULT 'Y' comment 'SMTP 보안연결 여부',
    biz_no              varchar(100)            null comment '사업자등록번호',
    domain_use_yn       char                    null default 'N' comment '회사도메인사용여부',
    reg_dt              datetime                null default current_timestamp() comment '등록일시',
    status              varchar(20)             null default '0' comment '서비스상태',
    holdings_no         int                     null comment '지주사번호',
    cntnt               varchar(2000)           null comment '상세내용',
    UNIQUE KEY UK1_TB_COMPANY(cmpny_id),
    INDEX IX1_TB_CMM_COMPANY (reg_dt, cmpny_nm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='회사정보';

create table tb_cmm_copy_hh(
	hh                  varchar(2) charset utf8mb4 null
);
create table tb_cmm_copy_y(
	yyyy                varchar(4)             null
);
create table tb_cmm_copy_ym(
	ym                  varchar(6)              null
);
create table tb_cmm_copy_ymd(
	ymd                 varchar(8)              null,
	ymd_date            datetime                null
);



create table tb_cmm_cstm_fld_grp(
	FLD_GRP_ID          varchar(50)         not null comment '필드그룹아이디',
	FLD_GRP_NM          varchar(50)             null comment '필드그룹명',
	MAP_TBL             varchar(50)             null comment '대상테이블아이디',
	MAP_FLD             varchar(50)             null comment '맵핑필드아이디',
	USE_YN              char                    null comment '사용여부',
	CMPNY_NO            int                 not null comment '회사번호',
	REG_DT              datetime                null comment '등록일시',
	UPD_DT              datetime                null comment '수정일시',
	primary key (CMPNY_NO, FLD_GRP_ID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='가변컬럼필드그룹';


create table tb_cmm_cstm_fld_data(
	FLD_GRP_ID          varchar(50)         not null comment '필드그룹아이디',
	FLD_GRP_SEQ         int not                 null comment '필드그룹일련번호',
	VARCHAR_50_1        varchar(50)             null,
	VARCHAR_50_2        varchar(50)             null,
	VARCHAR_50_3        varchar(50)             null,
	VARCHAR_50_4        varchar(50)             null,
	VARCHAR_50_5        varchar(50)             null,
	VARCHAR_50_6        varchar(50)             null,
	VARCHAR_50_7        varchar(50)             null,
	VARCHAR_50_8        varchar(50)             null,
	VARCHAR_50_9        varchar(50)             null,
	VARCHAR_50_10       varchar(50)             null,
	VARCHAR_100_1       varchar(100)            null,
	VARCHAR_100_2       varchar(100)            null,
	VARCHAR_100_3       varchar(100)            null,
	VARCHAR_100_4       varchar(100)            null,
	VARCHAR_100_5       varchar(100)            null,
	VARCHAR_100_6       varchar(100)            null,
	VARCHAR_100_7       varchar(100)            null,
	VARCHAR_100_8       varchar(100)            null,
	VARCHAR_100_9       varchar(100)            null,
	VARCHAR_100_10      varchar(100)            null,
	VARCHAR_200_1       varchar(200)            null,
	VARCHAR_200_2       varchar(200)            null,
	VARCHAR_200_3       varchar(200)            null,
	VARCHAR_200_4       varchar(200)            null,
	VARCHAR_200_5       varchar(200)            null,
	VARCHAR_200_6       varchar(200)            null,
	VARCHAR_200_7       varchar(200)            null,
	VARCHAR_200_8       varchar(200)            null,
	VARCHAR_200_9       varchar(200)            null,
	VARCHAR_200_10      varchar(200)            null,
	VARCHAR_MAX_1       varchar(4000)           null,
	VARCHAR_MAX_2       varchar(4000)           null,
	VARCHAR_MAX_3       varchar(4000)           null,
	VARCHAR_MAX_4       varchar(4000)           null,
	VARCHAR_MAX_5       varchar(4000)           null,
	VARCHAR_MAX_6       varchar(4000)           null,
	VARCHAR_MAX_7       varchar(4000)           null,
	VARCHAR_MAX_8       varchar(4000)           null,
	VARCHAR_MAX_9       varchar(4000)           null,
	VARCHAR_MAX_10      varchar(4000)           null,
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
	DECIMAL_1           decimal(18,2)           null,
	DECIMAL_2           decimal(18,2)           null,
	DECIMAL_3           decimal(18,2)           null,
	DECIMAL_4           decimal(18,2)           null,
	DECIMAL_5           decimal(18,2)           null,
	DECIMAL_6           decimal(18,2)           null,
	DECIMAL_7           decimal(18,2)           null,
	DECIMAL_8           decimal(18,2)           null,
	DECIMAL_9           decimal(18,2)           null,
	DECIMAL_10          decimal(18,2)           null,
	DATETIME_1          datetime                null,
	DATETIME_2          datetime                null,
	DATETIME_3          datetime                null,
	DATETIME_4          datetime                null,
	DATETIME_5          datetime                null,
	DATETIME_6          datetime                null,
	DATETIME_7          datetime                null,
	DATETIME_8          datetime                null,
	DATETIME_9          datetime                null,
	DATETIME_10         datetime                null,
	CMPNY_NO            int                 not null comment '회사번호',
	REG_DT              datetime                null comment '등록일시',
	UPD_DT              datetime                null comment '수정일시',
	primary key (FLD_GRP_ID, FLD_GRP_SEQ)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='가변컬럼필드데이터';

create table tb_cmm_cstm_fld_map(
	FLD_GRP_ID          varchar(50)         not null comment '필드그룹아이디',
	FLD_ID              varchar(50)         not null comment '필드아이디',
	FLD_NM              varchar(50)             null comment '필드명',
	REQUIRED_YN         varchar(1)              null comment '필수여부',
	SORT_NO             int                     null comment '순번',
	CMPNY_NO            int                 not null comment '회사번호',
	REG_DT              datetime                null comment '등록일시',
	UPD_DT              datetime                null comment '수정일시',
	REG_EMP_NO          int                     null comment '등록자사번',
	UPD_EMP_NO          int                     null comment '수정자사번',
	INPUT_TYPE          varchar(20)             null comment '입력유형',
	INPUT_TYPE_DETAIL   varchar(1000)           null comment '입력유형코드또는줄수',
	WIDTH_CD            varchar(20)             null comment '가로유형',
	FIND_YN             varchar(1)              null comment '검색조회필드여부',
	USE_YN              varchar(1)  default 'Y' null comment '사용여부',
	primary key (FLD_GRP_ID, FLD_ID, CMPNY_NO)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='가변필드화면맵핑';


create table tb_cmm_dept(
    dept_no         int               auto_increment comment '부서번호' primary key,
    cmpny_no        int                         null comment '회사번호',
    dept_cd         varchar(50)                 null comment '부서코드',
    dept_nm         varchar(100)                null comment '부서명',
    dpth            int                         null comment '깊이',
    sort_no         int                         null comment '정렬순서',
    use_yn          char(1)              DEFAULT 'Y' comment '사용여부',
    p_dept_no       int                         null comment '상위부서번호',
    reg_emp_no      int                     NOT NULL comment '등록자사번',
    reg_dt          datetime default current_timestamp() comment '등록일자',
    upd_emp_no      int                         null comment '수정자사번',
    upd_dt          datetime                    null comment '수정일자',
    INDEX IX1_TB_CMM_DEPT (cmpny_no, dept_no, sort_no),
    INDEX IX2_TB_CMM_DEPT (p_dept_no, sort_no),
    INDEX IX3_TB_CMM_DEPT (cmpny_no, dept_cd)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='부서';


create table tb_cmm_email_trans(
    email_seq_no        bigint        auto_increment comment '시퀀스' primary key,
    sender              varchar(50)         NOT NULL COMMENT '발신 메일 주소',
    title               varchar(200)        NOT NULL COMMENT '제목',
    msg                 longtext            NOT NULL COMMENT '내용',
    rg_date             datetime default current_timestamp() COMMENT '등록일시',
    rg_emp_no           int                 NOT NULL COMMENT '등록자',
    cmpny_no            int                 NOT NULL COMMENT '회사번호',
    INDEX IX1_TB_CMM_EMAIL_TRANS (rg_date, cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='이메일 발송 이력';


create table tb_cmm_email_receiver
(
    email_receiver_seq_no bigint auto_increment comment '시퀀스'
        primary key,
    email_seq_no bigint not null comment '이메일 발송이력 일련번호',
    email varchar(50) not null comment '수신 메일 주소',
    cust_no int null comment '고객번호',
    cust_nm varchar(50) null comment '고객명',
    rg_date datetime default current_timestamp() not null comment '등록일시',
    sent_yn char default 'N' null comment '발송성공여부',
    sent_at datetime null comment '발송성공일시'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '이메일 발송 수신자 이력';

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
    phone_user_id       varchar(36)         null comment 'IP폰아이디',
    phone_user_tel      varchar(15)         null comment '스테이션번호',
    postn_cd            varchar(36)         null comment '직위코드',
    duty_cd             varchar(36)         null comment '직책',
    cmpny_no            int                 null comment '회사번호',
    emp_sort            int                 null comment '직원표시순서',
    phone_extension_no  varchar(100)        null comment 'cic내선번호',
    email               varchar(50)         null comment '이메일',
    emp_tp              varchar(2)          null comment '사원구분',
    real_emp_nm         varchar(100)        null comment '실명',
    user_key            varchar(36)     not null comment '유저키(유니크)',
    UNIQUE KEY UK1_TB_CMM_EMP(user_key),
    INDEX IX1_TB_CMM_EMP (dept_no),
    INDEX IX2_TB_CMM_EMP (cmpny_no, rtrmnt_yn, emp_nm),
    index IX3_TB_CMM_EMP (cmpny_emp_cd, emp_no, emp_nm),
    INDEX IX4_TB_CMM_EMP (phone_user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사원';

create table if not exists tb_cmm_faq(
    no          int                   auto_increment comment 'faq일련번호' primary key,
    category1   varchar(20) charset utf8        null comment '대분류',
    category2   varchar(20) charset utf8        null comment '중분류',
    category3   varchar(20) charset utf8        null comment '소분류',
    title       varchar(200) charset utf8       null comment '제목',
    creator     int                             null comment '생성자',
    create_time datetime                        null comment '생성일시',
    modifier    int                             null comment '수정자',
    modify_time datetime                        null comment '수정일시',
    use_yn      char charset utf8               null comment '사용여부',
    del_yn      char charset utf8               null comment '삭제여부',
    best_yn     char charset utf8               null comment '인기여부',
    question    text charset utf8               null comment '질문',
    answer      text charset utf8               null comment '답변',
    cmpny_no    int                             null comment '회사번호',
    score_cnt   int                             null comment '만족도건수',
    score_sum   int                             null comment '만족도총합',
    INDEX IX1_TB_CMM_FAQ(category1),
    INDEX IX2_TB_CMM_FAQ(category2),
    INDEX IX3_TB_CMM_FAQ(category3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='FAQ';


create table tb_cmm_favorites(
    favoritesid         varchar(64)         not null primary key comment '즐겨찾기아이디',
    emp_no              int                     null comment '등록자사번',
    favoritesnm         varchar(400)            null comment '즐겨찾기제목',
    favoritesurl        varchar(400)            null comment '즐겨찾기경로',
    target              varchar(400)            null comment '타켓',
    height              varchar(100)            null comment '팝업높이',
    width               varchar(100)            null comment '팝업넓이',
    tops                varchar(100)            null comment '팝업상단위치',
    lefts               varchar(100)            null comment '팝업좌측위치',
    channelmodefg       varchar(1)  default '0' null comment '채널모드구분',
    directoriesfg       varchar(1)  default '0' null comment '디렉토리구분',
    fullscreenfg        varchar(1)  default '0' null comment '전체보기여부',
    locationfg          varchar(1)  default '0' null comment '로케이션바여부',
    menubarfg           varchar(1)  default '0' null comment '메뉴바여부',
    resizablefg         varchar(1)  default '0' null comment '리사이즈여부',
    scrollbarsfg        varchar(1)  default '0' null comment '스크롤여부',
    statusfg            varchar(1)  default '0' null comment '상태바여부',
    titlebarfg          varchar(1)  default '0' null comment '타이틀바여부',
    toolbarfg           varchar(1)  default '0' null comment '툴바여부',
    windowsetfg         varchar(1)  default '0' null comment '윈도창여부',
    systemfg            varchar(1)              null comment '시스템구분',
    ordno               int                     null comment '순서',
    center_cd           varchar(6)          not null comment '중앙여부',
    targetgrp           varchar(20) default 'LK00' null comment '타겟그룹'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='즐겨찾기';


CREATE TABLE tb_cmm_func_conf (
    seq_no              int(11)             NOT NULL AUTO_INCREMENT COMMENT '일련번호',
    conf_nm             varchar(200)            NULL COMMENT '설정명',
    conf_key            varchar(40)         NOT NULL COMMENT '설정키',
    del_yn              char(1)                 NULL COMMENT '삭제여부',
    PRIMARY KEY (seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상담메인화면관리';

CREATE TABLE tb_cmm_func_conf_map (
    seq_no              int(11)             NOT NULL AUTO_INCREMENT COMMENT '일련번호',
    conf_seq_no         int(11)             NOT NULL COMMENT '설정번호',
    conf_yn             char(1)             NOT NULL COMMENT '설정여부',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    PRIMARY KEY (seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상담메인화면관리맵핑';

CREATE TABLE tb_cmm_glbl_config (
    seq_no        int(11)             AUTO_INCREMENT COMMENT '일련번호' primary key,
    ky            varchar(50)                   NULL COMMENT 'key',
    val           varchar(1000)                 NULL COMMENT 'val',
    cmpny_no      int(11)                       NULL COMMENT '회사번호'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='환경설정';

create table tb_cmm_group (
    grp_no              int           auto_increment comment '그룹번호' primary key,
    cate_id             int                     not null comment '그룹카테고리',
    grp_nm              varchar(1000)           null comment '그룹명',
    use_yn              char                    null comment '사용여부',
    reg_dt              datetime                null default current_timestamp() comment '등록일시',
    upd_dt              datetime                null comment '수정일시',
    grp_cd              varchar(20)             null comment '그룹코드',
    reg_user_no         int                     null comment '등록자',
    upd_user_no         int                     null comment '수정자',
    cmpny_no            int                     null comment '회사번호',
    upd_enable_yn       char                    null comment '수정가능여부',
    callcenter_yn       varchar(20)             null comment 'Call Center 여부',
    UNIQUE KEY UK1_TB_GROUP(cmpny_no, grp_cd)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹';

create table tb_cmm_group_map(
    map_no              bigint        auto_increment comment '시퀀스' primary key,
    grp_no              int                 not null comment '그룹번호',
    data_gbn            char(1)             not null comment 'E:직원,D:부서',
    data_no             int                 not null comment '직원,부서번호',
    reg_emp_no          int                 not null comment '등록자',
    reg_dt              datetime default current_timestamp() comment '등록일시',
    use_yn              char(1) default 'Y' not null comment '사용여부',
    sort_no             int     default 0   not null comment '정렬순서',
    UNIQUE KEY UK1_TB_CMM_GROUP_MAP(grp_no, data_gbn, data_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹맵핑';


create table tb_cmm_group_map_hst(
    seq_no              bigint        auto_increment comment '시퀀스' primary key,
    map_no              int                 not null comment '매핑번호',
    grp_no              int                 not null comment '그룹번호',
    data_gbn            char(1)             not null comment 'E:직원,D:부서',
    data_no             int                 not null comment '직원,부서번호',
    action              char                not null comment '액션CRUD',
    reg_emp_no          int                 not null comment '등록자',
    reg_dt              datetime default current_timestamp() comment '등록일시',
    reg_ip              varchar(100)            null comment '등록자IP',
    cmpny_no            int                 not null comment '회사번호',
    INDEX IX1_TB_CMM_GROUP_MAP_HST (reg_dt, cmpny_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='그룹맵핑 히스토리';

create table tb_cmm_login(
    user_id             varchar(50)     not null comment '사용자아이디' primary key,
    emp_no              int             not null comment '사번',
    psswd               varchar(100)        null comment '패스워드',
    psswd_err_num       int             not null default 0 comment '패스워드오류횟수',
    psswd_upd_dt        datetime        not null default current_timestamp() comment '패스워드수정일시',
    last_login_dt       datetime            null comment '최종로그인일시',
    last_logout_dt      datetime            null comment '최종로그아웃일시',
    last_ip_address     varchar(40)         null comment '최종접속아이피',
    lnb_position        char                null comment '로컬네비게이션바_위치',
    call_rcv_type       char                null comment '전화수신방식',
    auto_peding_yn      char                null comment '자동대기여부',
    support_use_yn      char                null comment '서포트사용여부',
    support_id          varchar(50)         null comment '서포트아이디',
	survey_dt           datetime            null comment '설문일자',
    CONSTRAINT FK1_TB_CMM_LOGIN FOREIGN KEY (emp_no) REFERENCES tb_cmm_emp (emp_no),
    INDEX IX1_TB_CMM_LOGIN (emp_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인';

create table tb_cmm_login_hist(
    hist_no             int       auto_increment comment '이력번호' primary key,
    user_id             varchar(50)         null comment '아이디',
    login_dt            datetime            null comment '로그인일시',
    client_ip           varchar(40)         null comment '접속IP',
    cmpny_no            int                 null comment '회사번호',
    INDEX IX1_TB_CMM_LOGIN_HIST (user_id, login_dt),
    INDEX IX2_TB_CMM_LOGIN_HIST (login_dt)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그인이력';

create table tb_cmm_logout_hist(
    hist_no             int       auto_increment comment '이력번호' primary key,
    user_id             varchar(50)         null comment '아이디',
    logout_dt           datetime            null comment '로그아웃일시',
    cmpny_no            int                 null comment '회사번호',
    INDEX IX1_TB_CMM_LOGOUT_HIST (user_id, logout_dt)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='로그아웃이력';


CREATE TABLE tb_cmm_mymenu (
    seq_no              int(11)             NOT NULL AUTO_INCREMENT COMMENT '시퀀스',
    emp_no              int(11)             NOT NULL COMMENT '사번',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    id                  varchar(20)         NOT NULL COMMENT '메뉴아이디',
    menu_grp_id         varchar(20)             NULL COMMENT '그룹아이디',
    sort_no             int(11)             NOT NULL COMMENT '정렬순서',
    favorite_yn         char(1)          DEFAULT 'N' COMMENT '즐겨찾기여부',
    PRIMARY KEY (seq_no),
    INDEX IX1_TB_CMM_MYMENU (emp_no,cmpny_no),
    INDEX IX2_TB_CMM_MYMENU (menu_grp_id,id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사용자설정메뉴';


CREATE TABLE tb_cmm_mymenu_group (
    menu_grp_id         varchar(20)         NOT NULL COMMENT '그룹아이디',
    menu_grp_nm         varchar(50)         NOT NULL COMMENT '메뉴그룹명',
    emp_no              int(11)             NOT NULL COMMENT '사번',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    sort_no             int(11)             NOT NULL COMMENT '정렬순서',
    PRIMARY KEY (menu_grp_id),
    INDEX IX1_TB_CMM_MYMENU_GROUP (emp_no,cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='메뉴그룹';


CREATE TABLE tb_cmm_note_box (
    box_no          int(11)      NOT NULL COMMENT '보관함번호',
    box_sr_type     CHAR(1)         NULL COMMENT '송수신구분',
    snd_dt          datetime            NULL COMMENT '발신일시',
    cntnt           mediumtext            NULL COMMENT '내용',
    sndr_emp_no     int(11)          NULL COMMENT '발신자사번',
    sndr_emp_nm     VARCHAR(100)   NULL COMMENT '발신자명',
    reg_emp_no      int(11)          NULL COMMENT '보관자사번',
    reg_dt          datetime            NULL COMMENT '등록일시',
    note_no         int(11)          NULL COMMENT '원본쪽지번호',
	PRIMARY KEY (box_no),
    INDEX IX1_TB_NOTE_BOX (reg_emp_no, note_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='쪽지보관함';



CREATE TABLE tb_cmm_note_msg (
    note_no    		int(11)    NOT NULL COMMENT '쪽지번호',
    sndr_emp_no    	int(11)    NULL COMMENT '발신자사번',
    sndr_emp_nm    	VARCHAR(100)    NULL COMMENT '발신자명',
    cntnt    		mediumtext    NULL COMMENT '내용',
    snd_dt    		datetime    NULL COMMENT '보낸시간',
    attach_yn    	CHAR    NULL COMMENT '파일첨부여부',
    note_div_cd    	VARCHAR(20)    NULL COMMENT '쪽지분류코드',
    limited_dt    	datetime    NULL COMMENT '제한일자',
    del_yn    		CHAR(1)    DEFAULT 'N'	NULL COMMENT '쪽지',
	PRIMARY KEY (note_no),
    INDEX IX1_TB_NOTE_MSG (sndr_emp_no, note_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='쪽지보관함';


CREATE TABLE tb_cmm_note_rcvrs (
  rcvr_no       int(11)  NOT NULL COMMENT '일련번호',
  note_no       int(11)      NULL COMMENT '쪽지번호',
  rcvr_emp_no   int(11)      NULL COMMENT '수신자사번',
  rcvr_emp_nm   VARCHAR(100)  NULL COMMENT '수신자이름',
  read_yn       CHAR(1)     NULL COMMENT '읽음여부',
  read_dt       datetime        NULL COMMENT '읽음일시',
  r_del_yn      VARCHAR(20) DEFAULT 'N' COMMENT '수신자삭제여부',
  s_del_yn      VARCHAR(20) DEFAULT 'N' COMMENT '발신자삭제여부',
	PRIMARY KEY (rcvr_no),
    INDEX IX1_TB_NOTE_RCVRS (rcvr_emp_no, rcvr_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='쪽지보관함';



CREATE TABLE tb_cmm_script_group (
    script_grp_id       varchar(20)         NOT NULL COMMENT '스크립트그룹아이디',
    script_grp_nm       varchar(50)         NOT NULL COMMENT '스크립트그룹명',
    use_yn              char(1)          DEFAULT 'Y' COMMENT '사용여부',
    sort_no             int(11)   NOT NULL DEFAULT 0 COMMENT '정렬순서',
    cmpny_no            int(11)             NOT NULL COMMENT '회사번호',
    PRIMARY KEY (script_grp_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='스크립트그룹';

CREATE TABLE tb_cmm_script (
    script_no           int(11)             NOT NULL AUTO_INCREMENT COMMENT '스크립트번호',
    script_nm           varchar(50)         NOT NULL COMMENT '스크립트명',
    page_no             int(11)         DEFAULT NULL COMMENT '연결화면 번호',
    use_yn              char(1)          DEFAULT 'Y' COMMENT '사용여부',
    script_grp_id       varchar(20)         NOT NULL COMMENT '스크립트그룹아이디',
    tmpl_id             varchar(20)             NULL COMMENT '카카오알림톡아이디',
    PRIMARY KEY (script_no),
    FOREIGN KEY FK1_TB_CMM_SCRIPT(script_grp_id) REFERENCES tb_cmm_script_group (script_grp_id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='스크립트';


CREATE TABLE tb_cmm_script_item (
    seq_no              int(11)             NOT NULL AUTO_INCREMENT COMMENT '일련번호',
    title               varchar(1000)   DEFAULT NULL COMMENT '스크립트제목',
    contents            varchar(2000)   DEFAULT NULL COMMENT '스크립트내용',
    reg_dt              datetime        DEFAULT NULL COMMENT '생성일시',
    upd_dt              datetime        DEFAULT NULL COMMENT '수정일시',
    reg_emp_no          int(11)         DEFAULT NULL COMMENT '등록자',
    upd_emp_no          int(11)         DEFAULT NULL COMMENT '최종수정자',
    cmpny_no            int(11)         DEFAULT NULL COMMENT '회사번호',
    use_yn              char(1)          DEFAULT 'Y' COMMENT '사용여부',
    sort_no             int(11)   NOT NULL DEFAULT 0 COMMENT '정렬순서',
    script_no           int(11)             NOT NULL COMMENT '스크립트번호',
    PRIMARY KEY (seq_no),
    FOREIGN KEY FK1_TB_CMM_SCRIPT_ITEM(script_no) REFERENCES tb_cmm_script (script_no) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='스크립트항목';


create table tb_cmm_sitemenu(
	rid                 varchar(20)         not null comment '루트메뉴아이디',
	pid                 varchar(20)         not null comment '상위메뉴아이디',
	id                  varchar(20)         not null comment '메뉴아이디' primary key,
	display             char    default 'Y' not null comment '노출여부',
	name                varchar(50)             null comment '메뉴명',
	method              varchar(10) default 'GET' null comment 'HTTP Method',
	path                varchar(200)            null comment '경로',
	roles               varchar(300)            null comment '권한',
	paths               varchar(1000)           null comment '추가경로',
	params              varchar(1000)           null comment '파라미터',
	attributes          varchar(4000) default '{}' not null comment 'JSON type 속성',
	template            varchar(20)             null comment '템플릿이름',
	authorizer          varchar(20)             null comment '권한처리자이름',
	sort                int       default 1 not null comment '정렬순서',
	INDEX IX1_TB_CMM_SITEMENU (rid, sort)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='사이트메뉴';



create table tb_cmm_sms_receiver
(
    sms_receiver_seq_no bigint auto_increment comment '시퀀스'  primary key,
    sms_seq_no bigint not null comment '문자 발송이력 일련번호',
    mobile varchar(20) not null comment '수신 전화 번호',
    cust_no int null comment '고객번호',
    cust_nm varchar(50) null comment '고객명',
    rg_date datetime default current_timestamp() not null comment '등록일시',
    sent_yn char default 'N' null comment '발송성공여부',
    sent_at datetime null comment '발송일시'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '문자 발송 수신자 이력';


create table tb_cmm_sms_trans(
    sms_seq_no          bigint        auto_increment comment '시퀀스' primary key,
    sender              varchar(20)         NOT NULL COMMENT '발신 전화 번호',
    msg                 varchar(1000)       NOT NULL COMMENT '내용',
    rg_date             datetime default current_timestamp() COMMENT '등록일시',
    rg_emp_no           int                 NOT NULL COMMENT '등록자',
    cmpny_no            int                 NOT NULL COMMENT '회사번호',
    sms_type            varchar(20)         NOT NULL COMMENT '문자유형',
    INDEX IX1_TB_TB_CMM_SMS_TRANS (rg_date, cmpny_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='문자 발송 이력';

create table tb_cmm_stats_keyword (
	seq_no              int                     null comment '시퀀스',
	yyyy                varchar(4)              null comment '년도',
	mm                  varchar(2)              null comment '월',
	dd                  varchar(2)              null comment '일',
	hh                  varchar(2)              null comment '시간',
	cnslt_reg_dt        datetime                null comment '상담등록일시',
	cnslt_seq_no        int                     null comment '상담번호',
	cnslt_div_cd        varchar(36)             null comment '상담유형',
	keyword             varchar(200)            null comment '키워드',
	keyword_cnt         int                     null comment '키워드갯수',
	reg_dt              datetime                null comment '등록일시',
	cmpny_no            int                     null comment '회사번호',
	INDEX IX1_TB_STATS_KEYWORD (cmpny_no, keyword, cnslt_reg_dt, cnslt_seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='키워드집계';


create table tb_cmm_tmpl_contents(
    seq_no          int               AUTO_INCREMENT COMMENT '상용구번호' primary key,
    is_global       char(1)              DEFAULT 'N' comment '전체여부',
    data_flag       char                    NOT null comment '데이타구분(S,E,C)',
    ttl             varchar(1000)           NOT null comment '상용구제목',
    cntnt           text                    NOT null comment '상용구내용',
    emp_no          int                     not null comment '사번',
    reg_dt          datetime default current_timestamp() comment '등록일시',
    upd_dt          datetime                    null comment '수정일시',
    cmpny_no        int                     not null comment '회사번호',
    use_tp          varchar(20) DEFAULT 'BOILERPLATE' comment '사용구분',
    del_yn          char(1)              DEFAULT 'N' comment '삭제여부',
    INDEX IX2_TB_CMM_TMPL_CONTENTS (emp_no, seq_no),
    INDEX IX1_TB_CMM_TMPL_CONTENTS (cmpny_no, seq_no)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상용구';

create table tb_cmm_tmpl_contents_macro(
    emp_no          int                     not null comment '사번',
    data_flag       char                    not null comment '데이타구분',
    shortcut        varchar(20)             not null comment '단축키',
    seq_no          int                     not null comment '상용구번호',
    primary key (emp_no, data_flag, shortcut)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='상용구 단축키';

create table tb_cmm_tmpl_kakao
   (
    tmpl_id 	varchar(20)                 not null comment '템플릿아이디',
	ttl 		varchar(1000)               not null comment '템플릿제목',
	cntnt 		text                 	    not null comment '템플릿내용',
	emp_no 		int                 		not null comment '사번',
	reg_dt 		datetime default now()      not null comment '등록일시',
	upd_dt 		datetime                 	null comment '수정일시',
	cmpny_no 	int                 		not null comment '회사번호',
	del_yn 		char(1) default 'N'         not null comment '삭제여부',
	tmpl_key 	varchar(36)					not null comment '템플릿키',
    primary key (tmpl_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='알림톡';


create table if not exists tb_cmm_work_date(
    calendar_date       varchar(8)          NOT NULL COMMENT '달력일자',
    dd                  varchar(2)              NULL COMMENT '일',
    week                varchar(3)              NULL COMMENT '한글요일',
    work_yn             varchar(1)              NULL COMMENT '근무여부',
    holiday_nm          varchar(100)            NULL COMMENT '공휴일이름',
    ww                  varchar(2)              NULL,
    PRIMARY KEY (calendar_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='근무일자';


