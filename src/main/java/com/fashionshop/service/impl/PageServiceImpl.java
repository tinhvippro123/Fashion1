package com.fashionshop.service.impl;
import com.fashionshop.dto.admin.PageRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;

import com.fashionshop.model.Page;
import com.fashionshop.repository.PageRepository;
import com.fashionshop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    private PageRepository pageRepository;

    @Override
    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    @Override
    public Page findById(Long id) {
        return pageRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.PAGE_NOT_FOUND));
    }

    @Override
    public Optional<Page> findBySlug(String slug) {
        return pageRepository.findBySlugAndIsActiveTrue(slug);
    }

    @Override
    public List<Page> getActivePages() {
        return pageRepository.findByIsActiveTrue();
    }

    @Override
    public Page save(Page page) {
        return pageRepository.save(page);
    }

    @Override
    public void delete(Long id) {
        pageRepository.deleteById(id);
    }

    @Override
    public Page createPage(PageRequestDTO request) {
        Page entity = new Page();
        entity.setTitle(request.getTitle());
        entity.setSlug(request.getSlug());
        entity.setContent(request.getContent());
        entity.setIsActive(request.getIsActive());
        return save(entity);
    }

    @Override
    public Page updatePage(Long id, PageRequestDTO request) {
        Page entity = findById(id);
        entity.setTitle(request.getTitle());
        entity.setSlug(request.getSlug());
        entity.setContent(request.getContent());
        entity.setIsActive(request.getIsActive());
        return save(entity);
    }
}
