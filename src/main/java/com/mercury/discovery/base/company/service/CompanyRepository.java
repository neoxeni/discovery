package com.mercury.discovery.base.company.service;

import com.mercury.discovery.base.company.model.Company;
import com.mercury.discovery.base.company.model.CompanyRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CompanyRepository {
    List<Company> findAll(CompanyRequestDto companyRequestDto);

    Company getCompany(int cmpnyNo);

    int update(Company company);

    int insert(Company company);

    int updateEmailPassword(int cmpnyNo, String newPassword);

    int confirm(Company company);

    String getDomain(String cmpnyId);

    String getDomainsFindByEmail(String email);


    void deleteCompany(Integer cmpnyNo);
}
