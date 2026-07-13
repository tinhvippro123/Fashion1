package com.fashionshop.exception;



public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(500, "Lỗi há»‡ thá»‘ng không xác định", org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR),

    PRODUCT_NOT_FOUND(404, "Sản phẩm không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    CATEGORY_NOT_FOUND(404, "Danh mục không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    VALIDATION_ERROR(400, "Dá»¯ liệu không hợp lệ", org.springframework.http.HttpStatus.BAD_REQUEST),

    BAD_REQUEST(400, "YÃªu cáº§u không hợp lệ", org.springframework.http.HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(401, "ChÆ°a xác thực", org.springframework.http.HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(403, "Không có quyền truy cập", org.springframework.http.HttpStatus.FORBIDDEN),

    BANNER_NOT_FOUND(404, "Banner không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    COLOR_NOT_FOUND(404, "MÃ u sáº¯c không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    SIZE_NOT_FOUND(404, "KÃ­ch thÆ°á»›c không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    FAQ_NOT_FOUND(404, "FAQ không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    PAGE_NOT_FOUND(404, "Trang không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    USER_NOT_FOUND(404, "Người dùng không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND),

    ORDER_NOT_FOUND(404, "ĐÆ¡n hàng không tồn tại", org.springframework.http.HttpStatus.NOT_FOUND);

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

