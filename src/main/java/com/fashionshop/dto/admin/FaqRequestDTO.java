package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class FaqRequestDTO {
    @NotBlank(message = "CÃ¢u há»\ufffdi khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String question;
    @NotBlank(message = "CÃ¢u tráº£ lá»\ufffdi khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String answer;
    @NotNull(message = "Tráº¡ng thÃ¡i hoáº¡t Ä‘á»™ng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private Boolean isActive;
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
