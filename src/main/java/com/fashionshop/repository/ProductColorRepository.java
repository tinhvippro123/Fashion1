package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.ProductColor;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
}