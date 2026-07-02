package com.fashionshop.repository;

import com.fashionshop.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Optional<Page> findBySlugAndIsActiveTrue(String slug);
    Optional<Page> findBySlug(String slug);
}
