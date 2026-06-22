package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    org.springframework.data.domain.Page<Color> findByNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
}
