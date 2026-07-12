package com.fashionshop.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Lỗi hệ thống không xác định", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_FOUND(404, "Sản phẩm không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(404, "Danh mục không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(400, "Dữ liệu không hợp lệ", org.springframework.http.HttpStatus.BAD_REQUEST),
    BAD_REQUEST(400, "Yêu cầu không hợp lệ", org.springframework.http.HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(401, "Chưa xác thực", org.springframework.http.HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "Không có quyền truy cập", org.springframework.http.HttpStatus.FORBIDDEN);

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
