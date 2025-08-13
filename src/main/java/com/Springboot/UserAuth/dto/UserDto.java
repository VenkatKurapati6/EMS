package com.Springboot.UserAuth.dto;

public class UserDto {
	private String id;
    private String email;  
    private String password;
    private String fullname;
    private String role;   

    public UserDto(String email, String password, String fullname, String role) {
        super();
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    
    
    public String getId() {
		return id;
	}
    
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDto [email=" + email + ", password=" + password + ", fullname=" + fullname + ", role=" + role + "]";
    }
}
