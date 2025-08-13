package com.Springboot.UserAuth.Services;

import com.Springboot.UserAuth.Entity.Timesheet;
import com.Springboot.UserAuth.repositories.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


@Service
public class TimesheetService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    // Save a timesheet entry
    public Timesheet saveTimesheet(Timesheet timesheet) {
    	System.out.println("Saving Timesheet: " + timesheet);
        return timesheetRepository.save(timesheet);
    }

    public List<Timesheet> getTimesheetsForUserByMonth(String userId, LocalDate start, LocalDate end) {
        String yearMonth = start + "-" + end; 
        return timesheetRepository.findTimesheetsByUserAndMonth(userId, yearMonth);
    }
    
    public List<Timesheet> getTimesheetsByUserAndMonth(String userId, String month, String year) {
        return timesheetRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }
    
    public List<Timesheet> getTimesheetsByMonth(String userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return timesheetRepository.findByUserIdAndDateBetween(userId, start, end);
    }
    
    // Get all timesheets for a specific user by user ID
    public List<Timesheet> getTimesheetsByUserId(String userId) {
        return timesheetRepository.findByUserId(userId);
    }

    // Get a timesheet by ID
    public Optional<Timesheet> getTimesheetById(Long id) {
        return timesheetRepository.findById(id);
    }

    // Update timesheet entry
    public Timesheet updateTimesheet(Timesheet updatedTimesheet) {
        return timesheetRepository.save(updatedTimesheet);
    }
    
    public List<Timesheet> getAllPendingTimesheetsForManager() {
        return timesheetRepository.findByStatusAndSubmittedToManager("Pending", true);
    }
    
   
    
    public Timesheet findById(Long id) {
        return timesheetRepository.findById(id).orElseThrow(() -> new RuntimeException("Timesheet not found"));
    }

    
    public void save(Timesheet timesheet) {
        timesheetRepository.save(timesheet);
    }

    public void saveAll(List<Timesheet> timesheets) {
        timesheetRepository.saveAll(timesheets);
    }
    
    public void submitToManager(List<Timesheet> timesheets) {
        System.out.println("Submitting " + timesheets.size() + " timesheets to the manager...");
        
        // Ensure your submission logic here, like updating the status to 'Submitted to Manager'
        for (Timesheet timesheet : timesheets) {
            timesheet.setStatus("Submitted to Manager");
            // Add your repository save/update logic here
            timesheetRepository.save(timesheet);
            System.out.println("Timesheet with ID: " + timesheet.getId() + " updated to 'Submitted to Manager'");
        }
    }
    
    public List<Timesheet> getSubmittedTimesheetsForManager() {
        return timesheetRepository.findByStatus("Submitted to Manager");
    }


    // Delete timesheet entry by ID
    public void deleteTimesheet(Long id) {
        timesheetRepository.deleteById(id);
    }
    
    public List<Timesheet> getTimesheetsForUser1(String userId, String yearMonth) {
        try {
            YearMonth ym = YearMonth.parse(yearMonth); // e.g., "2025-04"
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();
            
            return timesheetRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } catch (Exception e) {
            System.err.println("❌ Error parsing yearMonth: " + yearMonth);
            return List.of(); // Return empty list if invalid format
        }
    }
    

	public List<Timesheet> getTimesheetsForUser(String userId, String yearMonth) {
    return timesheetRepository. findTimesheetsByUserAndMonth(userId, yearMonth);
}

	
	private String formatDecimalHoursAsHhMm(double decimalHours) {
		int hours=(int) decimalHours;
		int minutes=(int) Math.round((decimalHours - hours)*60);
		
		if(minutes == 60) {
			hours++;
			minutes =0;
		}
		return String.format("%02d:%02d",hours,minutes);
	}

	public List<Map<String, Object>> getWeeklySummaries(String userId) {
	    List<Timesheet> timesheets = timesheetRepository.findByUserId(userId);
	    System.out.println("📝 Total timesheets found: " + timesheets.size());

	    Map<LocalDate, List<Timesheet>> groupedByWeekStart = new HashMap<>();

	    for (Timesheet ts : timesheets) {
	        LocalDate date = ts.getDate();

	        int daysSinceMonday = date.getDayOfWeek().getValue() - 1;
	        LocalDate weekStart = date.minusDays(daysSinceMonday);

	        groupedByWeekStart.computeIfAbsent(weekStart, k -> new ArrayList<>()).add(ts);

	        LocalDate weekEnd = weekStart.plusDays(6);
	        System.out.println("✅ Timesheet Date: " + date + " → WeekStart: " + weekStart + ", WeekEnd: " + weekEnd);
	    }

	    Map<LocalDate, List<Timesheet>> sortedByRecentWeeks = new TreeMap<>(Comparator.reverseOrder());
	    sortedByRecentWeeks.putAll(groupedByWeekStart);

	    List<Map<String, Object>> weeklySummaries = new ArrayList<>();

	    for (Map.Entry<LocalDate, List<Timesheet>> entry : sortedByRecentWeeks.entrySet()) {
	        LocalDate weekStart = entry.getKey();
	        LocalDate weekEnd = weekStart.plusDays(6);
	        List<Timesheet> weekTimesheets = entry.getValue();

	        double totalHours = 0;
	        Set<String> uniqueStatuses = new HashSet<>();

	        for (Timesheet ts : weekTimesheets) {
	            try {
	                String[] parts = ts.getTotalHoursWorked().split(":");
	                int hours = Integer.parseInt(parts[0]);
	                int minutes = Integer.parseInt(parts[1]);
	                double worked = hours + (minutes / 60.0);

	                totalHours += worked;
	                System.out.println("⏱️ Parsed hours: " + worked + " for date " + ts.getDate());
	                
	                uniqueStatuses.add(ts.getStatus());

	            } catch (Exception e) {
	            	
	                System.out.println("❌ Failed to parse time for " + ts.getDate());
	            }
	        }

	        // Determine the final status for the week based on unique statuses found
	        String status;
	        if (uniqueStatuses.contains("Submitted to Manager")) {
	            status = "Submitted to Manager";
	        } else if (uniqueStatuses.contains("Approved") && uniqueStatuses.size() == 1) {
	            status = "Approved";
	        } else if (uniqueStatuses.contains("Rejected") && uniqueStatuses.size() == 1) {
	            status = "Rejected";
	        } else {
	            status = "Pending";
	        }

	        String weekRange = "Week Range: " + weekStart.format(DateTimeFormatter.ofPattern("dd MMM")) +
	                " to " + weekEnd.format(DateTimeFormatter.ofPattern("dd MMM"));

	        Map<String, Object> summary = new HashMap<>();
	        summary.put("weekRange", weekRange);
	        summary.put("weekStart", weekStart);
	        summary.put("weekEnd", weekEnd);
	        summary.put("totalHours", formatDecimalHoursAsHhMm(totalHours));
	        summary.put("status", status); // Set the calculated status
	        summary.put("pendingCount", weekTimesheets.stream().filter(ts -> "Pending".equalsIgnoreCase(ts.getStatus())).count());

	        // Print the summary to see what is being added
	        System.out.println("📅 Summary: " + weekRange + " → Total Hours: " + totalHours + ", Status: " + status);

	        weeklySummaries.add(summary);
	    }

	    return weeklySummaries;
	}


	public List<Timesheet> getTimesheetsForWeek(String userId, LocalDate weekStart, LocalDate weekEnd) {
		 System.out.println("🔍 Fetching timesheets for userId: " + userId + ", from " + weekStart + " to " + weekEnd);
	    return timesheetRepository.findByUserIdAndDateBetween(userId, weekStart, weekEnd);
	    
	}
	
	public List<Timesheet> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
	    return timesheetRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
	}


	public List<Timesheet> findByDateBetween(String weekRange) {
	    String[] dates = weekRange.split(" - ");
	    LocalDate startDate = LocalDate.parse(dates[0].trim());
	    LocalDate endDate = LocalDate.parse(dates[1].trim());

	    return timesheetRepository.findByDateBetween(startDate, endDate);
	}

}
	

