package com.mercury.discovery.base.company.web;

import com.mercury.discovery.base.company.model.Company;
import com.mercury.discovery.base.company.service.CompanyService;
import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.common.SimpleResponseModel;
import com.mercury.discovery.common.error.exception.BadParameterException;
import com.mercury.discovery.utils.MessagesUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${apps.request-mapping}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyRestController {
    private final CompanyService companyService;

    @GetMapping("/base/companiesMe")
    public ResponseEntity<?> getCompaniesMe(AppUser appUser) {
        Company company = companyService.getCompany(appUser.getCmpnyNo());
        if(company.getEmailPw() != null){
            company.setEmailPw("PROTECTED");
        }
        return ResponseEntity.ok(company);
    }

    @PatchMapping("/base/companies")
    public ResponseEntity<?> patchCompany(AppUser appUser, @RequestBody Company company) {
        company.setCmpnyNo(appUser.getCmpnyNo());
        int affected = companyService.update(company);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }

    @PatchMapping("/base/companies/email/password")
    public ResponseEntity<?> patchCompanyEmailPassword(AppUser appUser, String newPassword, String confirmPassword) {
        if(newPassword != null && !newPassword.equals(confirmPassword)){
            throw new BadParameterException("Not Match newPassword and confirmPassword");
        }

        int affected = companyService.updateEmailPassword(appUser.getCmpnyNo(), newPassword);
        return ResponseEntity.ok(new SimpleResponseModel(affected, MessagesUtils.getMessage("sentence.update")));
    }
}
