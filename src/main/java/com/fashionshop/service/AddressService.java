package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Address;

public interface AddressService {
	List<Address> getAddressesByUserId(Long userId);

	void addAddressToUser(Long userId, Address address);

	void deleteAddress(Long addressId);

	Address getAddressById(Long id);

	void updateAddress(Long addressId, Address address);
}