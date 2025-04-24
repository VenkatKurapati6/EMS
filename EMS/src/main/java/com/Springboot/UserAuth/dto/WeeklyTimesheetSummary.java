package com.Springboot.UserAuth.dto;

import java.time.LocalDate;

public class WeeklyTimesheetSummary {
    private LocalDate startOfWeek;
    private LocalDate endOfWeek;
    private String status;
    private String totalHours;

    public WeeklyTimesheetSummary(LocalDate startOfWeek, LocalDate endOfWeek, String status, String totalHours) {
        this.startOfWeek = startOfWeek;
        this.endOfWeek = endOfWeek;
        this.status = status;
        this.totalHours = totalHours;
    }

	public LocalDate getStartOfWeek() {
		return startOfWeek;
	}

	public void setStartOfWeek(LocalDate startOfWeek) {
		this.startOfWeek = startOfWeek;
	}

	public LocalDate getEndOfWeek() {
		return endOfWeek;
	}

	public void setEndOfWeek(LocalDate endOfWeek) {
		this.endOfWeek = endOfWeek;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(String totalHours) {
		this.totalHours = totalHours;
	}
}
