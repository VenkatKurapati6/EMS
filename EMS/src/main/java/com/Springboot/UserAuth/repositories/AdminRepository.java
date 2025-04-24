package com.Springboot.UserAuth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Springboot.UserAuth.Entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {
    
}
