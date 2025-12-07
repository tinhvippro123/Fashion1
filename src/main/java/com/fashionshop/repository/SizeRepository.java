package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

}