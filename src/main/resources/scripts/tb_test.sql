
INSERT INTO cmm_code_div (code, name, service, is_use, is_enable_update, description, client_id) VALUES ('OR01', '직책', 'common', 1, 1, null, -1);
INSERT INTO cmm_code_div (code, name, service, is_use, is_enable_update, description, client_id) VALUES ('OR02', '직위', 'common', 1, 1, null, -1);

INSERT INTO cmm_code (div_code, parent_code, code, name, sort, description, is_use, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR01', '0', 'OR01AB', '대표이사', 1, null, 1, 0, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_code, parent_code, code, name, sort, description, is_use, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR01', '0', 'OR01AC', '팀장', 2, null, 1, 0, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_code, parent_code, code, name, sort, description, is_use, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR02', '0', 'OR02AB', '사장', 1, null, 1, 0, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_code, parent_code, code, name, sort, description, is_use, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR02', '0', 'OR02AC', '이사', 2, null, 1, 0, now(), 1, now(), null, null, null, null, 1);


INSERT INTO client_user (id, created_at, created_by, duty_cd, email, extension_no, identification, is_retire, name, nickname, phone, position_cd, sort, status, updated_at, updated_by, user_key, client_id, department_id) VALUES (1, '2022-01-11 16:25:08', 0, null, 'cooper@mercuryproject.com', null, null, 0, '이정범', 'cooper', null, null, 0, 'ACTIVE', '2022-01-11 16:25:08', 0, 'dfkjhsdhvnksdjvbksbv', 1, null);




INSERT INTO cmm_code_div (div_cd, div_nm, div_service,  upd_enable_yn,  client_id) VALUES ('OR01', '직책', 'common', 'Y',  -1);
INSERT INTO cmm_code_div (div_cd, div_nm, div_service,  upd_enable_yn,  client_id) VALUES ('OR02', '직위', 'common', 'Y',  -1);

INSERT INTO cmm_code (div_cd, parent_code, cd, name, sort, description, use_yn, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR01', 'ROOT', 'OR01AB', '대표이사', 1, null, 'Y', 1, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_cd, parent_code, cd, name, sort, description, use_yn, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR01', 'ROOT', 'OR01AC', '팀장', 2, null, 'Y', 1, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_cd, parent_code, cd, name, sort, description, use_yn, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR02', 'ROOT', 'OR02AB', '사장', 1, null, 'Y', 1, now(), 1, now(), null, null, null, null, 1);
INSERT INTO cmm_code (div_cd, parent_code, cd, name, sort, description, use_yn, created_by, created_at, updated_by, updated_at, etc1, etc2, etc3, etc4, client_id) VALUES ('OR02', 'ROOT', 'OR02AC', '이사', 2, null, 'Y', 1, now(), 1, now(), null, null, null, null, 1);
