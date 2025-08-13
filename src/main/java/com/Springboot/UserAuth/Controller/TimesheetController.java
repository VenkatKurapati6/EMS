package com.Springboot.UserAuth.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.Springboot.UserAuth.Entity.Timesheet;
import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.Services.TimesheetService;
import com.Springboot.UserAuth.Services.UserService;
import com.Springboot.UserAuth.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@Controller
@RequestMapping("/timesheet")
public class TimesheetController {

	 @Autowired
	 private TimesheetRepository timesheetRepository;
	 
    @Autowired
    private TimesheetService timesheetService;

    @Autowired
    private UserService userService;
    
    @GetMapping("/TruTimeSheet")
    public String showTruTimeSheet(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();  

        System.out.println("🔍 Logged-in user email: " + userEmail);

        User user = userService.findByEmail(userEmail);
        if (user != null) {
            System.out.println("✅ User found: " + user);

            List<Timesheet> timesheets = timesheetService.getTimesheetsByUserId(user.getId());

            // ✅ Log ALL timesheet data for debugging
            System.out.println("📝 Retrieved Timesheets:");
            for (Timesheet t : timesheets) {
                System.out.println("   - ID: " + t.getId() +
                        ", Date: " + t.getDate() +
                        ", Login: " + t.getLoginTime() +
                        ", Logout: " + t.getLogoutTime() +
                        ", Break: " + t.getBreakMinutes() +
                        ", Total Hours: " + t.getTotalHoursWorked() +
                        ", Status: " + t.getStatus() +
                        ", Manager Comments: " + t.getManagerComments());
            }

            model.addAttribute("employeeId", user.getId());
            model.addAttribute("timesheets", timesheets);
        } else {
            System.out.println("❌ User not found for email: " + userEmail);
        }

        return "TruTimeSheet";
    }
    
    @GetMapping("/{userId}/{yearMonth}")
    public ResponseEntity<List<Timesheet>> getTimesheetsByUserAndMonth(
            @PathVariable String userId,
            @PathVariable String yearMonth) {
    	
        System.out.println("📥 Received request for: " + userId + " | " + yearMonth);  
        
        String[] parts = yearMonth.split("-");
        if (parts.length != 2) {
            System.out.println("❌ Invalid yearMonth format");
            return ResponseEntity.badRequest().build();
        }

        String year = parts[0];
        String month = parts[1];

        // Call your service method
        List<Timesheet> timesheets = timesheetService.getTimesheetsByUserAndMonth(userId, month, year);

        if (timesheets.isEmpty()) {
            System.out.println("⚠️ No timesheet found for: " + userId + " in " + month + "-" + year);
            return ResponseEntity.ok(Collections.emptyList()); // Returns 200 OK with []
        }

        System.out.println("✅ Returning " + timesheets.size() + " timesheets");
        return ResponseEntity.ok(timesheets); // Returns 200 OK + JSON
    }
    
    @PostMapping("/saveOrUpdate")
    public ResponseEntity<String> saveOrUpdateTimesheet(@RequestBody Timesheet timesheet) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ User not found.");
        }

        timesheet.setUserId(user.getId());
        timesheet.setLoginTime(timesheet.getLoginTime().withSecond(0).withNano(0));
        timesheet.setLogoutTime(timesheet.getLogoutTime().withSecond(0).withNano(0));

        Optional<Timesheet> existingTimesheet = timesheetRepository.findByUserIdAndDate(user.getId(), timesheet.getDate());

        try {
            if (existingTimesheet.isPresent()) {
                Timesheet existing = existingTimesheet.get();
                existing.setLoginTime(timesheet.getLoginTime());
                existing.setLogoutTime(timesheet.getLogoutTime());
                existing.setBreakMinutes(timesheet.getBreakMinutes());
                existing.setActivityLog(timesheet.getActivityLog());
                existing.setTotalHoursWorked(timesheet.getTotalHoursWorked());

                timesheetService.saveTimesheet(existing);
                return ResponseEntity.ok("✅ Timesheet updated successfully!");
            } else {
                timesheetService.saveTimesheet(timesheet);
                return ResponseEntity.ok("✅ New timesheet saved successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error processing timesheet: " + e.getMessage());
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkTimesheetExists(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        boolean exists = timesheetRepository.findByUserIdAndDate(user.getId(), date).isPresent();
        return ResponseEntity.ok(exists);
    }
  

    @GetMapping("/timesheetSummary")
    public String showTimesheetSummary(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "SubmitTimesheets";
        }

        List<Map<String, Object>> weeklySummaries =
            timesheetService.getWeeklySummaries(user.getId());
        model.addAttribute("weeklySummaries", weeklySummaries);
        return "SubmitTimesheets";
    }

    @GetMapping("/week/{start}/{end}")
    public String viewTimesheetsForWeek(
        @PathVariable("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart,
        @PathVariable("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd,
        Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "SubmitTimesheets";
        }

        // Fetch timesheets for the selected week
        List<Timesheet> timesheets = timesheetService.getTimesheetsForWeek(user.getId(), weekStart, weekEnd);

        // Add the timesheets and week info to the model
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("weeklyTimesheets", timesheets);
        System.out.println("📅 WeekStart: " + weekStart + ", WeekEnd: " + weekEnd);
        System.out.println("📋 Timesheets: " + timesheets);

        return "TimesheetDetails";
    }
    
    @PostMapping("/submitToManager")
    public String submitToManager(@RequestParam("weekStart") String weekStart, 
                                  @RequestParam("weekEnd") String weekEnd,
                                  Principal principal) {

        // Print received request parameters
        System.out.println("Received request to submit timesheets to manager for week: " + weekStart + " to " + weekEnd);

        // Fetch the user based on the logged-in principal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());

        if (user == null) {
            System.out.println("User not found for email: " + auth.getName());
            return "errorPage";  // Redirect to a custom error page
        }

        System.out.println("User found: " + user.getFullname());

        // Fetch the timesheets for the given week range
        List<Timesheet> timesheets = timesheetService.getTimesheetsForWeek(user.getId(), 
                                                                           LocalDate.parse(weekStart), 
                                                                           LocalDate.parse(weekEnd));

        if (timesheets.isEmpty()) {
            System.out.println("No timesheets found for userId: " + user.getId() + " from " + weekStart + " to " + weekEnd);
        } else {
            System.out.println("Fetched " + timesheets.size() + " timesheets for the user from " + weekStart + " to " + weekEnd);
        }

        // Process the timesheets: send them to the manager
        timesheetService.submitToManager(timesheets);
        System.out.println("Timesheets for userId: " + user.getId() + " have been submitted to the manager.");

        // After submission, redirect to a summary or confirmation page
        System.out.println("Redirecting to timesheet summary after submission...");
        return "redirect:/timesheet/timesheetSummary";  // Redirect to the timesheet summary page
    }

    


} 