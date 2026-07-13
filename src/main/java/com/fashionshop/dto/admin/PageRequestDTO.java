package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class PageRequestDTO {
    @NotBlank(message = "TiÃªu Ä‘á»\ufffd khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String title;
    @NotBlank(message = "Slug khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String slug;
    @NotBlank(message = "Ná»™i dung khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String content;
    @NotNull(message = "Tráº¡ng thÃ¡i hoáº¡t Ä‘á»™ng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private Boolean isActive;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
