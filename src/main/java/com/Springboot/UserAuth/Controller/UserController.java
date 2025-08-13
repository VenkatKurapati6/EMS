package com.Springboot.UserAuth.Controller;

import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.Services.UserService;
import com.Springboot.UserAuth.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName()); // Fetch user by email
        model.addAttribute("userdetail", user);
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }
    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        try {
            // Check if email already exists
            User existingUser = userService.findByEmail(userDto.getEmail());
            if (existingUser != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email " + userDto.getEmail() + " already exists!");
                return "redirect:/register";
            }
            
            User existingUserById = userService.findById(userDto.getId());
            if (existingUserById != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Employee ID " + userDto.getId() + " already exists!");
                return "redirect:/register";
            }

            // ✅ Validate Role Selection
            if (userDto.getRole() == null || userDto.getRole().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a role before registering.");
                return "redirect:/register";
            }

            // Save the new user
            userService.save(userDto);
            redirectAttributes.addFlashAttribute("successMessage", "Employee with Email " + userDto.getEmail() + " registered successfully!");

            return "redirect:/register";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "redirect:/register";
        }
    }


    @GetMapping("/redirectAfterLogin")
    public String redirectAfterLogin(HttpServletRequest request) {
        User user = userService.findByEmail(request.getUserPrincipal().getName());

        if (user != null) {
            String role = user.getRole();
            switch (role) {
                case "Admin":
                    return "redirect:/admin/dashboard";
                case "Manager":
                    return "redirect:/manager/dashboard";
                case "Employee":
                    return "redirect:/employee/dashboard";
                case "HR":
                    return "redirect:/hr/dashboard";
                default:
                    return "redirect:/login?error=unauthorized";
            }
        }
        return "redirect:/login";
    }
}


