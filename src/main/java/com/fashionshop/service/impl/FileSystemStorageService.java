package com.fashionshop.service.impl;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

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
			throw new FashionShopException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Could not initialize storage");
		}
	}

	@Override
	public String store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "Failed to store empty file.");
			}

			// Chỉ cho phép các định dạng ảnh an toàn (Whitelist)
			String extension = "";
			String originalFilename = file.getOriginalFilename();
			if (originalFilename != null) {
				int i = originalFilename.lastIndexOf('.');
				if (i > 0) {
					extension = originalFilename.substring(i).toLowerCase();
				}
			}

			if (!extension.matches("^\\.(jpg|jpeg|png|gif|webp)$")) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "Định dạng file không được hỗ trợ. Chỉ chấp nhận ảnh (jpg, jpeg, png, gif, webp).");
			}

			String newFilename = UUID.randomUUID().toString() + extension;
			Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

			// Ngăn chặn Path Traversal Vulnerability
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "Không thể lưu file ngoài thư mục cho phép (Path Traversal Attempt).");
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}

			return newFilename;
		} catch (IOException e) {
			throw new FashionShopException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to store file.");
		}
	}

	@Override
	public void delete(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new FashionShopException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to delete file.");
		}
	}
}