package com.fashionshop.enums;

public enum OrderStatus {
	PENDING, // Chờ xử lý (Mới đặt)
	CONFIRMED, // Đã xác nhận (Admin duyệt)
	SHIPPING, // Đang giao hàng
	COMPLETED, // Giao thành công
	CANCELLED, // Đã hủy
	RETURNED // Trả hàng
}