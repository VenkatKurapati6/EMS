package com.Springboot.UserAuth.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hr")
public class HrController {

    @GetMapping("/dashboard")
    public String hrDashboard() {
        return "hr_dashboard"; }
    
}
