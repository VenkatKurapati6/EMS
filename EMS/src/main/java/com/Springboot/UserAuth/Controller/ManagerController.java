package com.Springboot.UserAuth.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.Springboot.UserAuth.Configuration.CustomUserDetails;

@Controller
public class ManagerController {

    @GetMapping("/manager/dashboard")
    public String managerDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("fullname", userDetails.getFullname());
        return "manager_dashboard"; // This will look for manager_dashboard.html in templates
    }
}
