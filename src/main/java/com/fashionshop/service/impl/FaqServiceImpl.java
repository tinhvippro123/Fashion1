package com.fashionshop.service.impl;
import com.fashionshop.dto.admin.FaqRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;

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
        return faqRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.FAQ_NOT_FOUND));
    }

    @Override
    public Faq saveFaq(Faq faq) {
        return faqRepository.save(faq);
    }

    @Override
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    @Override
    public Faq createFaq(FaqRequestDTO request) {
        Faq entity = new Faq();
        entity.setQuestion(request.getQuestion());
        entity.setAnswer(request.getAnswer());
        entity.setStatus(request.getIsActive());
        return saveFaq(entity);
    }

    @Override
    public Faq updateFaq(Long id, FaqRequestDTO request) {
        Faq entity = getFaqById(id);
        entity.setQuestion(request.getQuestion());
        entity.setAnswer(request.getAnswer());
        entity.setStatus(request.getIsActive());
        return saveFaq(entity);
    }


}
