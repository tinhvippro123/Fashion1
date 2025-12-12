package com.fashionshop.dto;

public class UserRegisterDTO {
	// Thông tin từ Form
	private String lastName; // Họ
	private String firstName; // Tên
	private String email;
	private String phoneNumber;
	private String dob; // Ngày sinh (String từ input date)
	private String gender;

	// Địa chỉ
	private String city; // Form gửi lên name="city" (Tương ứng province trong DB)
	private String district;
	private String ward;
	private String detailAddress; // Form gửi lên name="detailAddress" (Tương ứng street trong DB)

	// Mật khẩu
	private String password;
	private String confirmPassword;

	public UserRegisterDTO() {
	}

	// --- GETTER & SETTER (No Lombok) ---
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}