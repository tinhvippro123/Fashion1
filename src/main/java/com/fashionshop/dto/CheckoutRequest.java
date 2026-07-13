package com.fashionshop.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {

    @NotBlank(message = "TÃªn ngÆ°á»\ufffdi nháº­n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String receiverName;

    @NotBlank(message = "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String phone;

    @NotBlank(message = "Tá»‰nh/ThÃ nh phá»‘ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String province;

    @NotBlank(message = "Quáº­n/Huyá»‡n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String district;

    @NotBlank(message = "PhÆ°á»\ufffdng/XÃ£ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String ward;

    @NotBlank(message = "Sá»‘ nhÃ , tÃªn Ä‘Æ°á»\ufffdng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
    private String street;

    private String note;
    private String paymentMethod = "COD";

    // Getters and Setters
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
