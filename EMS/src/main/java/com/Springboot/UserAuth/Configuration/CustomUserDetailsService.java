package com.Springboot.UserAuth.Configuration;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Springboot.UserAuth.Entity.User;
import com.Springboot.UserAuth.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Attempting login with email: " + email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("❌ User not found!");
            throw new UsernameNotFoundException("Invalid Email or Password");
        }

        System.out.println("✅ User found: " + user.getEmail());
        System.out.println("🔐 Stored password (hashed): " + user.getPassword());
        System.out.println("🎭 Role: " + user.getRole());

        return new CustomUserDetails(
            user.getEmail(), 
            user.getPassword(), 
            Collections.singletonList(new SimpleGrantedAuthority(user.getRole())), 
            user.getFullname()
        );
    }
}
