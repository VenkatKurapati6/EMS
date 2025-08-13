package com.Springboot.UserAuth.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

import com.Springboot.UserAuth.Converter.TimeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "timesheets")
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;  // employee id from user_details table

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "login_time", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    @Convert(converter = TimeConverter.class) // Force trimming seconds
    private LocalTime loginTime;

    @Column(name = "logout_time", nullable = false)
    @JsonFormat(pattern = "HH:mm")
    @Convert(converter = TimeConverter.class) // Force trimming seconds
    private LocalTime logoutTime;


    @Column(name = "break_time")
    private Integer breakMinutes; 

    @Column(name = "total_hours_worked", nullable = false)
    private String totalHoursWorked; // ✅ Stores "HH:mm" format
    

    @Column(name = "activity_log", length = 1000)
    private String activityLog;
    
 
    @Column(name = "status", nullable = false)
    private String status = "Pending"; // Default status when submitted

    @Column(name = "manager_comments")
    private String managerComments;
    
    @Column(name = "submitted_to_manager")
    private Boolean submittedToManager = false;

    @Column(name = "submitted_date")
    private LocalDate submittedDate;
    

    
    @PrePersist
    @PreUpdate
    public void trimSecondsFromTime() {
        if (loginTime != null) {
          loginTime = LocalTime.of(loginTime.getHour(), loginTime.getMinute());
        }
        if (logoutTime != null) {
            logoutTime = LocalTime.of(logoutTime.getHour(), logoutTime.getMinute());
        }
    }



    public Timesheet() {}

    public Timesheet(String userId, LocalDate date, LocalTime loginTime, LocalTime logoutTime, Integer breakMinutes, String totalHoursWorked, String activityLog, String status, String managerComments,Boolean submittedToManager, LocalDate submittedDate) {
        this.userId = userId;
        this.date = date;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.breakMinutes = breakMinutes;
        this.totalHoursWorked = totalHoursWorked;
        this.activityLog = activityLog;
        this.status = status;
        this.managerComments = managerComments;
        this.submittedToManager = submittedToManager;
        this.submittedDate = submittedDate;
     
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Integer getBreakMinutes() {
		return breakMinutes;
	}

	public void setBreakMinutes(Integer breakMinutes) {
		this.breakMinutes = breakMinutes;
	}

	public String getTotalHoursWorked() {
		return totalHoursWorked;
	}

	public void setTotalHoursWorked(String totalHoursWorked) {
		this.totalHoursWorked = totalHoursWorked;
	}

	public String getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(String activityLog) {
        this.activityLog = activityLog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerComments() {
        return managerComments;
    }

    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }

	public Boolean getSubmittedToManager() {
		return submittedToManager;
	}
	
	public void setSubmittedToManager(Boolean submittedToManager) {
		this.submittedToManager = submittedToManager;
	}
	
	public LocalDate getSubmittedDate() {
		return submittedDate;
	}
	
	public void setSubmittedDate(LocalDate submittedDate) {
		this.submittedDate = submittedDate;
	}



    
}
