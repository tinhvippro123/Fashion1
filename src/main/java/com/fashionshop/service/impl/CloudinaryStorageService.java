package com.fashionshop.service.impl;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fashionshop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Primary
public class CloudinaryStorageService implements StorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public void init() {
        // Cloudinary không cần tạo thư mục local
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FashionShopException(ErrorCode.BAD_REQUEST, "Failed to store empty file.");
            }

            // Upload ảnh lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            // Lấy URL an toàn (HTTPS) trả về để lưu vào Database
            return uploadResult.get("secure_url").toString();
            
        } catch (IOException e) {
            throw new FashionShopException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to store file on Cloudinary.");
        }
    }

    @Override
    public void delete(String filename) {
        try {
            // filename có thể là URL (https://res.cloudinary.com/.../image/upload/v12345/public_id.jpg)
            // hoặc tên file cũ (uuid.jpg). 
            // Nếu là URL của Cloudinary, ta cần trích xuất public_id để xóa
            if (filename != null && filename.contains("res.cloudinary.com")) {
                String publicId = extractPublicId(filename);
                if (publicId != null && !publicId.isEmpty()) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            } else {
                // Nếu là file cũ lưu ở máy, có thể bỏ qua hoặc gọi FileSystemStorageService để xóa
                // Tạm thời bỏ qua
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file on Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        // Ví dụ URL: https://res.cloudinary.com/dan2b8knu/image/upload/v1700000000/abcd1234xyz.jpg
        // Ta cần lấy "abcd1234xyz"
        try {
            int lastSlashIndex = url.lastIndexOf('/');
            int dotIndex = url.lastIndexOf('.');
            if (lastSlashIndex != -1 && dotIndex != -1 && lastSlashIndex < dotIndex) {
                return url.substring(lastSlashIndex + 1, dotIndex);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
