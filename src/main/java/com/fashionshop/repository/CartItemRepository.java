package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Hiện tại JpaRepository đã có sẵn các hàm cơ bản như:
    // save(), deleteById(), findById()...
    // Nên tạm thời chúng ta chưa cần viết thêm query phức tạp ở đây.
    
    // Ví dụ nếu sau này cần xóa hết item trong 1 giỏ:
    // void deleteByCartId(Long cartId);
}