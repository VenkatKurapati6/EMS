package com.Springboot.UserAuth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Springboot.UserAuth.Entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email); // Changed to find by email instead of username
}

