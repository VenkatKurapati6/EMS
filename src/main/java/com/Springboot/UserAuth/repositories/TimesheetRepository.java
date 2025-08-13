package com.Springboot.UserAuth.repositories;

import com.Springboot.UserAuth.Entity.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
    // Fetch all timesheets for a specific user by their ID
    List<Timesheet> findByUserId(String userId);
    Optional<Timesheet> findByUserIdAndDate(String userId, LocalDate date);
    List<Timesheet> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    List<Timesheet> findByUserIdAndStatusAndDateBetween(String userId, String status, LocalDate startDate, LocalDate endDate);
    List<Timesheet> findByUserIdAndStatus(String userId, String status);
    

    @Query("SELECT t FROM Timesheet t WHERE t.userId = :userId AND FUNCTION('MONTHNAME', t.date) = :month AND FUNCTION('YEAR', t.date) = :year")
    List<Timesheet> findByUserIdAndMonthAndYear(@Param("userId") String userId, @Param("month") String month, @Param("year") String year);
    
    List<Timesheet> findByStatusAndSubmittedToManager(String status, boolean submittedToManager);

    List<Timesheet> findByStatus(String status);
    
    
    
    @Query("SELECT t FROM Timesheet t WHERE t.userId = :userId AND FUNCTION('DATE_FORMAT', t.date, '%Y-%m') = :yearMonth")
    List<Timesheet> findTimesheetsByUserAndMonth(@Param("userId") String userId, @Param("yearMonth") String yearMonth);
	List<Timesheet> findByDateBetween(LocalDate startDate, LocalDate endDate);
   
}
