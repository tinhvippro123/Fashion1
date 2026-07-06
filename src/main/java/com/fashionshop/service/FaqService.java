package com.fashionshop.service;

import com.fashionshop.model.Faq;
import java.util.List;

public interface FaqService {
    List<Faq> getAllFaqs();
    List<Faq> searchFaqs(String keyword);
    List<Faq> getActiveFaqs();
    Faq getFaqById(Long id);
    Faq saveFaq(Faq faq);
    void deleteFaq(Long id);
}
