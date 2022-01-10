package com.mercury.discovery.base.organization.web;

import com.mercury.discovery.common.log.security.SecurityLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
@SecurityLogging()
public class IndexController {
    @GetMapping("/base/organization/index")
    public String index() {
        return "vue/components/base/organization/UbBaseOrganization";
    }
}