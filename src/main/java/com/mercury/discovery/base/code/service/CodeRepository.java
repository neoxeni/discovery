package com.mercury.discovery.base.code.service;

import com.mercury.discovery.base.code.model.Code;
import com.mercury.discovery.base.code.model.CodeDiv;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CodeRepository {

    List<Code> findAll(Code code);

    List<Code> findByDiv(String divCd, Integer cmpnyNo);

    List<CodeDiv> findCodeDivAll(Code code);

    int insert(Code code);

    int update(Code code);

    int delete(Code code);

    int deleteCodesByDivCd(CodeDiv codeDiv);

    int insertCodeDiv(CodeDiv codeDiv);

    int updateCodeDiv(CodeDiv codeDiv);

    int deleteCodeDiv(CodeDiv codeDiv);

    int merge(List<Code> codes);
}
