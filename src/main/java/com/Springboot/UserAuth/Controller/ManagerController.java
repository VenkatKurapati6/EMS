package com.Springboot.UserAuth.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.Springboot.UserAuth.Configuration.CustomUserDetails;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.Springboot.UserAuth.Entity.Timesheet;
import com.Springboot.UserAuth.Services.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	@Autowired
	private TimesheetService timesheetService;
	

    @GetMapping("/dashboard")
    public String managerDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("fullname", userDetails.getFullname());
        return "manager_dashboard"; // This will look for manager_dashboard.html in templates
    }
    
    @GetMapping("/pending-timesheets")
    public String viewPendingTimesheets(Model model) {
        List<Timesheet> submitted = timesheetService.getSubmittedTimesheetsForManager();

        // Group by userId + week range (Monday to Sunday)
        Map<String, List<Timesheet>> grouped = submitted.stream().collect(Collectors.groupingBy(ts -> {
            LocalDate date = ts.getDate();
            LocalDate weekStart = date.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            return ts.getUserId() + "|" + weekStart + " to " + weekEnd;
        }));

        // Check if the grouped data is being passed correctly
        System.out.println("Grouped Timesheets: " + grouped);  // Debugging step

        // Add grouped data to the model
        model.addAttribute("weeklyGroupedTimesheets", grouped);

     
        return "managerApproval";
    }
    
    @PostMapping("/timesheet/handle")
    public String handleTimesheetApproval(
            @RequestParam("timesheetId") Long timesheetId,
            @RequestParam("userId") String userId,
            @RequestParam("date") String dateStr,
            @RequestParam("action") String action,
            @RequestParam(value = "comment", required = false) String comment) {

        try {
            LocalDate date = LocalDate.parse(dateStr);  // Parse date safely
            Timesheet timesheet = timesheetService.findById(timesheetId);

            if (timesheet == null) {
                // Handle missing timesheet
                return "redirect:/manager/dashboard?error=notfound";
            }

            if ("approve".equalsIgnoreCase(action)) {
                timesheet.setStatus("Approved");
                System.out.println("Approved");
            } else if ("reject".equalsIgnoreCase(action)) {
                timesheet.setStatus("Rejected");
                System.out.println("Rejected");
                timesheet.setManagerComments("No comment provided.");
                
            }

            timesheetService.save(timesheet);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manager/dashboard?error=invalid_date";
        }

        return "redirect:/manager/pending-timesheets";
    }

    @PostMapping("/timesheet/handleAll")
    public String handleApproveAll(
            @RequestParam("weekRange") String weekRange,
            @RequestParam("action") String action) {

        try {
            // Split weekRange into employee ID and date range
            String[] parts = weekRange.split("\\|");  // Split into employee ID and date range
            String userId = parts[0];  // Employee ID
            String[] dates = parts[1].trim().split(" to ");  // Split the date range into start and end

            // Parse the dates into LocalDate
            LocalDate startDate = LocalDate.parse(dates[0].trim());
            LocalDate endDate = LocalDate.parse(dates[1].trim());

            // Fetch timesheets based on user ID and date range
            List<Timesheet> timesheets = timesheetService.findByUserIdAndDateRange(userId, startDate, endDate);

            for (Timesheet timesheet : timesheets) {
                if ("approveAll".equalsIgnoreCase(action)) {
                    timesheet.setStatus("Approved");
                    System.out.println("approved for " + weekRange);
                } else if ("rejectAll".equalsIgnoreCase(action)) {
                    timesheet.setStatus("Rejected");
                    System.out.println("Rejected for " + weekRange);
                    timesheet.setManagerComments("No comment provided.");
                }
            }

            // Save updated timesheets
            timesheetService.saveAll(timesheets);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/manager/dashboard?error=handleAll";
        }

        return "redirect:/manager/pending-timesheets";
    }

}
