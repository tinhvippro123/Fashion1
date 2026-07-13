package com.fashionshop.service;

import com.fashionshop.model.Page;
import com.fashionshop.dto.admin.PageRequestDTO;
import java.util.List;
import java.util.Optional;

public interface PageService {
    List<Page> findAll();
    Page findById(Long id);
    Optional<Page> findBySlug(String slug);
    List<Page> getActivePages();
    Page save(Page page);
    void delete(Long id);
    Page createPage(PageRequestDTO request);
    Page updatePage(Long id, PageRequestDTO request);
}
