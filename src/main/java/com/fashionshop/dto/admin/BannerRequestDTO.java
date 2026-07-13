package com.fashionshop.dto.admin;
import org.springframework.web.multipart.MultipartFile;
public class BannerRequestDTO {
    private String targetUrl;
    private String position;
    private MultipartFile imageFile;
    private Boolean isActive;
    private Integer displayOrder;
    
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public MultipartFile getImageFile() { return imageFile; }
    public void setImageFile(MultipartFile imageFile) { this.imageFile = imageFile; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
