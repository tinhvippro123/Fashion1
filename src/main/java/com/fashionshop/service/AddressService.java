package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Address;
import com.fashionshop.model.User;

public interface AddressService {
	List<Address> getAddressesByUserId(Long userId);

	void addAddressToUser(Long userId, Address address);

//	admin and client
	void deleteAddress(Long addressId);
	Address getAddressById(Long id);

//	admin
	void updateAddress(Long addressId, Address address);
	
//	Client
	List<Address> findByUser(User user);
    void save(Address address); // Client tự save chính mình
    void setDefaultAddress(Long addressId, Long userId);
	
    Address getDefaultAddress(User user);
    
}