package com.fashionshop.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void init(); // Tạo folder upload nếu chưa có

	String store(MultipartFile file); // Lưu file và trả về tên file

	void delete(String filename); // Xóa file
}