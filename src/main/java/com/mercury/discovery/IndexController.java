package com.mercury.discovery;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Controller
public class IndexController {

    @Value("${apps.index:thymeleaf/index}")
    private String appIndex;

    @GetMapping({"", "/"})
    public String index() {
        return appIndex;
    }

    @GetMapping("/login")
    public String login() {
        return "thymeleaf/login";
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }
}