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
		// áº¢nh sáº½ lÆ°u vÃ o folder "uploads" náº±m ngay thÆ° má»¥c gá»‘c dá»± Ã¡n
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

			// Chá»‰ cho phÃ©p cÃ¡c Ä‘á»‹nh dáº¡ng áº£nh an toÃ n (Whitelist)
			String extension = "";
			String originalFilename = file.getOriginalFilename();
			if (originalFilename != null) {
				int i = originalFilename.lastIndexOf('.');
				if (i > 0) {
					extension = originalFilename.substring(i).toLowerCase();
				}
			}

			if (!extension.matches("^\\.(jpg|jpeg|png|gif|webp)$")) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "Ä\ufffdá»‹nh dáº¡ng file khÃ´ng Ä‘Æ°á»£c há»— trá»£. Chá»‰ cháº¥p nháº­n áº£nh (jpg, jpeg, png, gif, webp).");
			}

			String newFilename = UUID.randomUUID().toString() + extension;
			Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();

			// NgÄƒn cháº·n Path Traversal Vulnerability
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "KhÃ´ng thá»ƒ lÆ°u file ngoÃ i thÆ° má»¥c cho phÃ©p (Path Traversal Attempt).");
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