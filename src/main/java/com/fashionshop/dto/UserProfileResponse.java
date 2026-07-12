package com.fashionshop.dto;

import com.fashionshop.enums.UserGender;
import com.fashionshop.enums.UserRole;
import com.fashionshop.model.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private UserGender gender;
    private LocalDate dateOfBirth;
    private UserRole role;
    
    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.role = user.getRole();
    }
}
