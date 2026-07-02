package com.fashionshop.service;

import com.fashionshop.model.Page;
import java.util.List;
import java.util.Optional;

public interface PageService {
    List<Page> findAll();
    Page findById(Long id);
    Optional<Page> findBySlug(String slug);
    Page save(Page page);
    void delete(Long id);
}
