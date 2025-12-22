package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.enums.UserRole;
import com.fashionshop.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    long countByRole(UserRole role);
}