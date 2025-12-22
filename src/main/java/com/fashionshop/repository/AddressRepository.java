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
    
 // Đếm số địa chỉ để biết có phải cái đầu tiên không
    long countByUserId(Long userId);
    
    List<Address> findByUser(User user);
    
 // Set toàn bộ địa chỉ của user thành "không mặc định"
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultAddresses(Long userId);
}