package com.fashionshop.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Lá»—i há»‡ thá»‘ng khÃ´ng xÃ¡c Ä‘á»‹nh", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_FOUND(404, "Sáº£n pháº©m khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(404, "Danh má»¥c khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(400, "Dá»¯ liá»‡u khÃ´ng há»£p lá»‡", org.springframework.http.HttpStatus.BAD_REQUEST),
    BAD_REQUEST(400, "YÃªu cáº§u khÃ´ng há»£p lá»‡", org.springframework.http.HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "ChÆ°a xÃ¡c thá»±c", org.springframework.http.HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "KhÃ´ng cÃ³ quyá»\ufffdn truy cáº­p", org.springframework.http.HttpStatus.FORBIDDEN),
    BANNER_NOT_FOUND(404, "Banner khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    COLOR_NOT_FOUND(404, "MÃ u sáº¯c khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    SIZE_NOT_FOUND(404, "KÃ­ch thÆ°á»›c khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    FAQ_NOT_FOUND(404, "FAQ khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    PAGE_NOT_FOUND(404, "Trang khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404, "NgÆ°á»\ufffdi dÃ¹ng khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(404, "Ä\ufffdÆ¡n hÃ ng khÃ´ng tá»“n táº¡i", org.springframework.http.HttpStatus.NOT_FOUND);
    private final int code;
    private final String description;
    private final org.springframework.http.HttpStatus statusCode;

    private ErrorCode(int code, String description, org.springframework.http.HttpStatus statusCode) {
        this.code = code;
        this.description = description;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public org.springframework.http.HttpStatus getStatusCode() {
        return this.statusCode;
    }
}
