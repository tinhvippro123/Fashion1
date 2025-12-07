package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.VariantImage;

@Repository
public interface VariantImageRepository extends JpaRepository<VariantImage, Long> {
}
