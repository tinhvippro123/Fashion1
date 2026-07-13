package com.fashionshop.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void init(); // Táº¡o folder upload náº¿u chÆ°a cÃ³

	String store(MultipartFile file); // LÆ°u file vÃ  tráº£ vá»\ufffd tÃªn file

	void delete(String filename); // XÃ³a file
}