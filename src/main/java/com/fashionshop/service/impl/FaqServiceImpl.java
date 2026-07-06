package com.fashionshop.service.impl;

import com.fashionshop.model.Faq;
import com.fashionshop.repository.FaqRepository;
import com.fashionshop.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqServiceImpl implements FaqService {

    @Autowired
    private FaqRepository faqRepository;

    @Override
    public List<Faq> getAllFaqs() {
        return faqRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
    }

    @Override
    public List<Faq> searchFaqs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFaqs();
        }
        return faqRepository.findByQuestionContainingIgnoreCaseOrderByDisplayOrderAscCreatedAtDesc(keyword.trim());
    }

    @Override
    public List<Faq> getActiveFaqs() {
        return faqRepository.findByStatusOrderByDisplayOrderAscCreatedAtDesc(true);
    }

    @Override
    public Faq getFaqById(Long id) {
        return faqRepository.findById(id).orElse(null);
    }

    @Override
    public Faq saveFaq(Faq faq) {
        return faqRepository.save(faq);
    }

    @Override
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }
}
