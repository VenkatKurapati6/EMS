package com.Springboot.UserAuth.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Admin {

    @Id
    @Column(name = "id") // ✅ Ensure it matches User ID
    private String id;  // ✅ This ID will come from UserDetails table

    private String employeeName;
    private String employeeEmail;
    private String employeePhone;
    private String employeeGender;
    private String employeeRole;


    // ✅ Constructor
    public Admin() {}

    public Admin(String id, String employeeName, String employeeEmail, String employeePhone, String employeeGender, String employeeRole) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeePhone = employeePhone;
        this.employeeGender = employeeGender;
        this.employeeRole = employeeRole;
    }

    // ✅ Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }

    public String getEmployeePhone() { return employeePhone; }
    public void setEmployeePhone(String employeePhone) { this.employeePhone = employeePhone; }

    public String getEmployeeGender() { return employeeGender; }
    public void setEmployeeGender(String employeeGender) { this.employeeGender = employeeGender; }

    public String getEmployeeRole() { return employeeRole; }
    public void setEmployeeRole(String employeeRole) { this.employeeRole = employeeRole; }
}
