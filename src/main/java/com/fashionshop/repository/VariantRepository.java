package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Variant;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
}
