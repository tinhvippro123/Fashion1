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
        // Cloudinary khÃ´ng cáº§n táº¡o thÆ° má»¥c local
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FashionShopException(ErrorCode.BAD_REQUEST, "Failed to store empty file.");
            }

            // Upload áº£nh lÃªn Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            // Láº¥y URL an toÃ n (HTTPS) tráº£ vá»\ufffd Ä‘á»ƒ lÆ°u vÃ o Database
            return uploadResult.get("secure_url").toString();
            
        } catch (IOException e) {
            throw new FashionShopException(ErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to store file on Cloudinary.");
        }
    }

    @Override
    public void delete(String filename) {
        try {
            // filename cÃ³ thá»ƒ lÃ  URL (https://res.cloudinary.com/.../image/upload/v12345/public_id.jpg)
            // hoáº·c tÃªn file cÅ© (uuid.jpg). 
            // Náº¿u lÃ  URL cá»§a Cloudinary, ta cáº§n trÃ­ch xuáº¥t public_id Ä‘á»ƒ xÃ³a
            if (filename != null && filename.contains("res.cloudinary.com")) {
                String publicId = extractPublicId(filename);
                if (publicId != null && !publicId.isEmpty()) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            } else {
                // Náº¿u lÃ  file cÅ© lÆ°u á»Ÿ mÃ¡y, cÃ³ thá»ƒ bá»\ufffd qua hoáº·c gá»\ufffdi FileSystemStorageService Ä‘á»ƒ xÃ³a
                // Táº¡m thá»\ufffdi bá»\ufffd qua
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file on Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        // VÃ­ dá»¥ URL: https://res.cloudinary.com/dan2b8knu/image/upload/v1700000000/abcd1234xyz.jpg
        // Ta cáº§n láº¥y "abcd1234xyz"
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
