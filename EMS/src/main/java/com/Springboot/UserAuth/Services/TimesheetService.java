package com.Springboot.UserAuth.Services;

import com.Springboot.UserAuth.Entity.Timesheet;
import com.Springboot.UserAuth.repositories.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	        int daysSinceSaturday = (date.getDayOfWeek().getValue() + 1) % 7;
	        LocalDate weekStart = date.minusDays(daysSinceSaturday);

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
	        int approvedCount = 0, rejectedCount = 0, pendingCount = 0;

	        for (Timesheet ts : weekTimesheets) {
	            try {
	                String[] parts = ts.getTotalHoursWorked().split(":");
	                int hours = Integer.parseInt(parts[0]);
	                int minutes = Integer.parseInt(parts[1]);
	                double worked = hours + (minutes / 60.0);

	                totalHours += worked;
	                System.out.println("⏱️ Parsed hours: " + worked + " for date " + ts.getDate());

	                String status = ts.getStatus();
	                if ("Approved".equalsIgnoreCase(status)) {
	                    approvedCount++;
	                } else if ("Rejected".equalsIgnoreCase(status)) {
	                    rejectedCount++;
	                } else {
	                    pendingCount++; // default case
	                }

	            } catch (Exception e) {
	                System.out.println("❌ Failed to parse time for " + ts.getDate());
	            }
	        }

	        String status = (approvedCount == weekTimesheets.size()) ? "Approved" :
	                        (rejectedCount == weekTimesheets.size()) ? "Rejected" : "Pending";

	        String weekRange = "Week Range: " + weekStart.format(DateTimeFormatter.ofPattern("dd MMM")) +
	                " to " + weekEnd.format(DateTimeFormatter.ofPattern("dd MMM"));

	        Map<String, Object> summary = new HashMap<>();
	        summary.put("weekRange", weekRange);
	        summary.put("weekStart", weekStart);
	        summary.put("weekEnd",   weekEnd);
	        summary.put("totalHours", formatDecimalHoursAsHhMm(totalHours));
	        summary.put("status", status);
	        summary.put("pendingCount", pendingCount);

	        System.out.println("📅 Summary: " + weekRange + " → Total Hours: " + totalHours + ", Status: " + status);

	        weeklySummaries.add(summary);
	    }

	    return weeklySummaries;
	}

	public List<Timesheet> getTimesheetsForWeek(String userId, LocalDate weekStart, LocalDate weekEnd) {
		 System.out.println("🔍 Fetching timesheets for userId: " + userId + ", from " + weekStart + " to " + weekEnd);
	    return timesheetRepository.findByUserIdAndDateBetween(userId, weekStart, weekEnd);
	    
	}


}
	

