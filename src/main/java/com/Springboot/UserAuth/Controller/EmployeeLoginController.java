package com.Springboot.UserAuth.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.repositories.UserRepository;


@Controller
@RequestMapping("/employee")
public class EmployeeLoginController {

    private final UserRepository userRepository;

    public EmployeeLoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            User user = userRepository.findByEmail(email); 
            if (user != null) {
                model.addAttribute("fullname", user.getFullname());
                model.addAttribute("employeeEmail", email);
            }
        }
        return "employee_dashboard";
    }
}
