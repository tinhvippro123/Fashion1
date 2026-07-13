package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Address;
import com.fashionshop.model.User;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    
 // Ä\ufffdáº¿m sá»‘ Ä‘á»‹a chá»‰ Ä‘á»ƒ biáº¿t cÃ³ pháº£i cÃ¡i Ä‘áº§u tiÃªn khÃ´ng
    long countByUserId(Long userId);
    
    List<Address> findByUser(User user);
    
 // Set toÃ n bá»™ Ä‘á»‹a chá»‰ cá»§a user thÃ nh "khÃ´ng máº·c Ä‘á»‹nh"
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultAddresses(Long userId);
}