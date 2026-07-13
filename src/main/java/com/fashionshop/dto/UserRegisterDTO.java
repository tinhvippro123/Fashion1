package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {

	@NotBlank(message = "Há»\ufffd khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	private String lastName;

	

	@NotBlank(message = "TÃªn khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	private String firstName;

	

	@NotBlank(message = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	@Email(message = "Email khÃ´ng há»£p lá»‡")

	private String email;

	

	@NotBlank(message = "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	private String phoneNumber;

	

	private String dob;

	private String gender;



	private String city;

	private String district;

	private String ward;

	private String detailAddress;



	@NotBlank(message = "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	@Size(min = 6, message = "Máº­t kháº©u pháº£i tá»« 6 kÃ½ tá»± trá»Ÿ lÃªn")

	private String password;

	

	@NotBlank(message = "XÃ¡c nháº­n máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

	private String confirmPassword;

}