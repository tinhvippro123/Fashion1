package com.fashionshop.repository;

import com.fashionshop.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByStatusOrderByDisplayOrderAscCreatedAtDesc(boolean status);
    List<Faq> findAllByOrderByDisplayOrderAscCreatedAtDesc();
    List<Faq> findByQuestionContainingIgnoreCaseOrderByDisplayOrderAscCreatedAtDesc(String keyword);
}
