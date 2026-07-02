package com.fashionshop.service;

import com.fashionshop.model.Contact;
import java.util.List;

public interface ContactService {
    List<Contact> findAll();
    Contact findById(Long id);
    Contact save(Contact contact);
    void delete(Long id);
}
