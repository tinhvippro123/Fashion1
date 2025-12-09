package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	// Tìm đơn hàng theo User ID (để xem lịch sử mua hàng)
	List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}