package com.mercury.discovery.base.company.service;

import com.mercury.discovery.base.company.model.Company;
import com.mercury.discovery.base.company.model.CompanyRequestDto;
import com.mercury.discovery.util.AESUtils;
import com.mercury.discovery.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final AESUtils aesUtils;

    @Transactional(readOnly = true)
    public List<Company> findAll(CompanyRequestDto companyRequestDto) {
        return companyRepository.findAll(companyRequestDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "company", key = "#cmpnyNo")
    public Company getCompany(Integer cmpnyNo) {
        return companyRepository.getCompany(cmpnyNo);
    }

    @CacheEvict(cacheNames = "company", key = "#company.cmpnyNo")
    public int update(Company company) {
        this.encryptPassword(company);
        return companyRepository.update(company);
    }

    public int insert(Company company) {
        this.encryptPassword(company);
        return companyRepository.insert(company);
    }

    @CacheEvict(cacheNames = "company", key = "#cmpnyNo")
    public int updateEmailPassword(int cmpnyNo, String newPassword) {
        return companyRepository.updateEmailPassword(cmpnyNo, aesUtils.encrypt(newPassword));
    }

    @CacheEvict(cacheNames = "company", key = "#company.cmpnyNo")
    public int confirm(Company company) {
        return companyRepository.confirm(company);
    }

    public String getDomain(String cmpnyId) {
        return companyRepository.getDomain(cmpnyId);
    }

    public String getDomainsFindByEmail(String email) {
        return companyRepository.getDomainsFindByEmail(email);
    }

    @CacheEvict(cacheNames = "company", key = "#cmpnyNo")
    public void deleteCompany(Integer cmpnyNo) {
        String profileActive = ContextUtils.getEnvironmentProperty("spring.profiles.active");
        if ("local".equals(profileActive)) {
            companyRepository.deleteCompany(cmpnyNo);
        } else {
            throw new IllegalArgumentException("Who Are You??");
        }
    }

    private void encryptPassword(Company company) {
        String emailPwd = company.getEmailPw();
        if (StringUtils.hasLength(emailPwd)) {
            company.setEmailPw(aesUtils.encrypt(emailPwd));
        }
    }
}
