package com.fashionshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fashionshop.model.Address;
import com.fashionshop.model.User;
import com.fashionshop.repository.AddressRepository;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.service.AddressService;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Address> getAddressesByUserId(Long userId) {
		return addressRepository.findByUserId(userId);
	}

	@Override
	public void addAddressToUser(Long userId, Address address) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// Logic: Gán user cho address
		address.setUser(user);

		// Logic: Nếu đây là địa chỉ mặc định, cần set các địa chỉ khác của user thành
		// false (nếu cần)
		if (Boolean.TRUE.equals(address.getIsDefault())) {
			List<Address> existingAddresses = addressRepository.findByUserId(userId);
			for (Address addr : existingAddresses) {
				addr.setIsDefault(false);
				addressRepository.save(addr);
			}
		}

		addressRepository.save(address);
	}

	@Override
	public void deleteAddress(Long addressId) {
		addressRepository.deleteById(addressId);
	}

	@Override
	public Address getAddressById(Long id) {
		return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
	}

	@Override
	public void updateAddress(Long addressId, Address addressDetails) {
		Address existingAddress = getAddressById(addressId);
		// Logic: Chỉ update các trường cho phép
		existingAddress.setReceiverName(addressDetails.getReceiverName());
		existingAddress.setPhone(addressDetails.getPhone());
		existingAddress.setProvince(addressDetails.getProvince());
		existingAddress.setDistrict(addressDetails.getDistrict());
		existingAddress.setWard(addressDetails.getWard());
		existingAddress.setStreet(addressDetails.getStreet());
		existingAddress.setIsDefault(addressDetails.getIsDefault());

		// Nếu set default mới, phải bỏ default cũ (gọi lại logic cũ hoặc tách hàm
		// private)
		if (Boolean.TRUE.equals(existingAddress.getIsDefault())) {
			// Logic bỏ default cũ của user sở hữu address này...
		}
		addressRepository.save(existingAddress);
	}
}