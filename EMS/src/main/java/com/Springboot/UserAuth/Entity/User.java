package com.Springboot.UserAuth.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "UserDetails") // Ensure this matches your table name
public class User {

    
	@Id
    @Column(name = "id", nullable = false)
	private String id;

    @Column(unique = true, nullable = false) // Ensure email is unique and not null
    private String email;

    @JsonIgnore
    private String password;

    private String fullname;

    @NotBlank(message = "Role is required")
    @Column(nullable = false) // Ensure role is not null
    private String role;

    // Default constructor
    public User() {}

    public User(String email, String password, String fullname, String role) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", fullname=" + fullname + ", role=" + role + "]";
    }
}
