package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//	Tìm đơn hàng theo User ID (để xem lịch sử mua hàng)
	List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
	
//	tra cứu đơn hàng
	Optional<Order> findByIdAndPhone(Long id, String phone);
	
	long countByStatus(OrderStatus status);
	
	@Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    Double sumTotalRevenue();
	
	@Query("SELECT SUM(o.totalAmount) FROM Order o WHERE CAST(o.orderDate AS date) = :date AND o.status = 'COMPLETED'")
	Double sumRevenueByDate(@Param("date") LocalDate date);
}