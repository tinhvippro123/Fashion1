package com.fashionshop.exception;

public class FashionShopException extends RuntimeException {
    private final ErrorCode errorCode;

    public FashionShopException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public FashionShopException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
