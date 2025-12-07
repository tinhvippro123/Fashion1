package com.fashionshop.service.impl;

import com.fashionshop.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	public FileSystemStorageService() {
		// Ảnh sẽ lưu vào folder "uploads" nằm ngay thư mục gốc dự án
		this.rootLocation = Paths.get("uploads");
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage", e);
		}
	}

	@Override
	public String store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file.");
			}

			// Đổi tên file ngẫu nhiên để tránh trùng (ví dụ: avatar.jpg -> uuid-avatar.jpg)
			String originalFilename = file.getOriginalFilename();
			String extension = "";
			int i = originalFilename.lastIndexOf('.');
			if (i > 0) {
				extension = originalFilename.substring(i);
			}

			String newFilename = UUID.randomUUID().toString() + extension;

			Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}

			return newFilename;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file.", e);
		}
	}

	@Override
	public void delete(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete file.", e);
		}
	}
}