package com.Springboot.UserAuth.Services;

import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.dto.UserDto;

public interface UserService {
    User findByEmail(String email); // Changed from username to email
    User save(UserDto userDto);
	User findById(String id);
}
