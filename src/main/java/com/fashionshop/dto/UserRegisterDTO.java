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

	@NotBlank(message = "Họ không được để trống")

	private String lastName;

	

	@NotBlank(message = "Tên không được để trống")

	private String firstName;

	

	@NotBlank(message = "Email không được để trống")

	@Email(message = "Email không hợp lệ")

	private String email;

	

	@NotBlank(message = "Số điện thoại không được để trống")

	private String phoneNumber;

	

	private String dob;

	private String gender;



	private String city;

	private String district;

	private String ward;

	private String detailAddress;



	@NotBlank(message = "Mật khẩu không được để trống")

	@Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")

	private String password;

	

	@NotBlank(message = "Xác nhận mật khẩu không được để trống")

	private String confirmPassword;

}