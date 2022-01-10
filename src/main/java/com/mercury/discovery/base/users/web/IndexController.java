package com.mercury.discovery.base.users.web;

import com.mercury.discovery.base.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Slf4j
@Controller
public class IndexController {

    private final UserService userService;

    @RequestMapping("/changePassword")
    public String changePassword(Model model, HttpServletRequest req, HttpServletResponse res) {

        int error = (int)req.getAttribute("error");
        String userId = (String)req.getAttribute("userId");
        int empNo = (int)req.getAttribute("empNo");

        model.addAttribute("userId",userId);
        model.addAttribute("empNo",empNo);
        model.addAttribute("error", error);
        return "thymeleaf/changePassword";
    }

    @PostMapping("/changePasswordOk")
    public String changePasswordOk(Model model, String empNo, String passwd) {

        if(empNo != null) {
            userService.resetUserPassword(Integer.parseInt(empNo), passwd);
        }

        return "redirect:/intro";
    }
}
