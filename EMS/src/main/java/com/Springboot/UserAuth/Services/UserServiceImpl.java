package com.Springboot.UserAuth.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.dto.UserDto;
import com.Springboot.UserAuth.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByEmail(String email) { // Changed from username to email
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User findById(String id) { // ✅ Add this method
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null); // Returns user if found, otherwise null
    }

    @Override
    public User save(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId()); // Set id
        user.setEmail(userDto.getEmail()); // Set email
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Encode password
        user.setFullname(userDto.getFullname());
        user.setRole(userDto.getRole()); // Set role

        // Print user for debugging
        System.out.println("Saving user: " + user);

        return userRepository.save(user);
    }

}
