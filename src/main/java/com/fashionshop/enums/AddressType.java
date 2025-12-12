package com.fashionshop.enums;

public enum AddressType {
    HOME("Nhà riêng / Chung cư"),
    OFFICE("Cơ quan / Công ty");

    private final String label;

    AddressType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}