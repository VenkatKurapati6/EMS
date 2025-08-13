package com.Springboot.UserAuth.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Springboot.UserAuth.Entity.ConfirmationForm;
import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.Entity.Admin;
import com.Springboot.UserAuth.repositories.AdminRepository;
import com.Springboot.UserAuth.repositories.UserRepository;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository employeeRepo;

    @Autowired
    private UserRepository userRepository;  // Fetches IDs from user_details table

 // display the html page
    @GetMapping("/admin/dashboard")
    public String getIndex(Model model) {
        List<Admin> employeeList = employeeRepo.findAll();
        model.addAttribute("employees", employeeList);
        model.addAttribute("employee", new Admin());
        model.addAttribute("confirmationForm", new ConfirmationForm());
        return "admin_dashboard";
    }



    // ✅ Fetch all Employee IDs for the Dropdown
    @GetMapping("/get-employee-ids")
    @ResponseBody
    public List<String> getEmployeeIds() {
        return userRepository.findAll().stream()
                .map(User::getId)  
                .collect(Collectors.toList());
    }

    // ✅ Fetch Employee Details by ID (Auto-fill Fullname & Email)
    @GetMapping("/get-employee-details")
    @ResponseBody
    public Map<String, String> getEmployeeDetails(@RequestParam String id) {
        Map<String, String> response = new HashMap<>();
        userRepository.findById(id).ifPresent(user -> {
            response.put("fullname", user.getFullname());
            response.put("email", user.getEmail());
        });
        return response;
    }
    
    

    // ✅ Save New Employee into Employee Table
    @PostMapping("/create")
    public String newEmployee(@ModelAttribute Admin employee, RedirectAttributes redirectAttributes) {
        Optional<Admin> existingEmployee = employeeRepo.findById(employee.getId());
        if (existingEmployee.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee ID " + employee.getId() + " already exists!");
            return "redirect:/admin/dashboard";
        }

        if (employee.getEmployeeRole() == null || employee.getEmployeeRole().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a role before adding the employee.");
            return "redirect:/admin/dashboard";
        }

        employeeRepo.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee with ID " + employee.getId() + " added successfully!");
        return "redirect:/admin/dashboard";
    }

    // Update existing employee
    @PostMapping("/update")
    public String updateEmployee(@ModelAttribute Admin employee, RedirectAttributes redirectAttributes) {
        Optional<Admin> existingEmployee = employeeRepo.findById(employee.getId());

        if (existingEmployee.isPresent()) {
            employeeRepo.save(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee with ID " + employee.getId() + " updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee with ID " + employee.getId() + " not found.");
        }
        return "redirect:/admin/dashboard";
    }

    @Autowired
    private AdminRepository adminRepository; // Manages employee table

    @PostMapping("/remove")
    public String removeEmployee(@RequestParam("id") String employeeId, RedirectAttributes redirectAttributes) {
        boolean deleted = false;

        // Delete from employee table (if exists)
        if (adminRepository.existsById(employeeId)) {
            adminRepository.deleteById(employeeId);
            deleted = true;
        }

        // Delete from user_details table (if exists)
        if (userRepository.existsById(employeeId)) {
            userRepository.deleteById(employeeId);
            deleted = true;
        }

        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Employee with ID " + employeeId + " deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee with ID " + employeeId + " not found.");
        }

        return "redirect:/admin/dashboard";
    }



}
