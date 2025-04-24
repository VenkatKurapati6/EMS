package com.Springboot.UserAuth.Controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/employee")
public class EmployeeLoginController {

    // Show Employee Login Page
    @GetMapping("/login")
    public String showEmployeeLoginPage() {
        return "login"; // Returns the employee login HTML page
    }
    @GetMapping("/dashboard") // ✅ Corrected mapping (NOT `/employee/employee/dashboard`)
    public String employeeDashboard(Model model, Principal principal) {
        if (principal != null) { // Ensure user is logged in
            String loggedInUser = principal.getName(); // Get logged-in user's email
            model.addAttribute("employeeEmail", loggedInUser); // ✅ Now works correctly
        }
        return "employee_dashboard"; // Ensure `templates/employee_dashboard.html` exists
    }
}
