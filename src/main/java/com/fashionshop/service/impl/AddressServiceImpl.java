package com.fashionshop.service.impl;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//  Common
	@Override
	public Address getAddressById(Long id) {
		return addressRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.BAD_REQUEST, "Address not found"));
	}

	@Override
	public void deleteAddress(Long addressId) {
		addressRepository.deleteById(addressId);
	}

//  Admin
	@Override
	public List<Address> getAddressesByUserId(Long userId) {
		return addressRepository.findByUserId(userId);
	}

	@Override
	@Transactional
	public void addAddressToUser(Long userId, Address address) {
		User user = userRepository.findById(userId).orElseThrow(() -> new FashionShopException(ErrorCode.UNAUTHENTICATED, "User not found"));
		address.setUser(user);

		save(address);
	}

	@Override
	@Transactional
	public void updateAddress(Long userId, Address addressDetails) {
//      Láº¥y ID tá»« chÃ­nh object form gá»­i lÃªn (do cÃ³ input hidden name="id")
		Long addressId = addressDetails.getId();

//      TÃ¬m Ä‘á»‹a chá»‰ cÅ© trong DB
		Address existingAddress = addressRepository.findById(addressId)
				.orElseThrow(() -> new FashionShopException(ErrorCode.BAD_REQUEST, "Ä\ufffdá»‹a chá»‰ khÃ´ng tá»“n táº¡i"));

//         Kiá»ƒm tra báº£o máº­t:
//         NgÆ°á»\ufffdi Ä‘ang Ä‘Äƒng nháº­p (userId) cÃ³ pháº£i lÃ  chá»§ cá»§a Ä‘á»‹a chá»‰ nÃ y khÃ´ng?
		if (!existingAddress.getUser().getId().equals(userId)) {
			throw new FashionShopException(ErrorCode.UNAUTHORIZED, "Báº¡n khÃ´ng cÃ³ quyá»\ufffdn sá»­a Ä‘á»‹a chá»‰ nÃ y!");
		}

//      Logic xá»­ lÃ½ Máº·c Ä‘á»‹nh
//      Náº¿u user chá»\ufffdn cÃ¡i nÃ y lÃ  máº·c Ä‘á»‹nh -> Reset háº¿t cÃ¡c cÃ¡i khÃ¡c
		if (Boolean.TRUE.equals(addressDetails.getIsDefault())) {
			addressRepository.resetDefaultAddresses(userId);
			existingAddress.setIsDefault(true);
		} else {
			// Náº¿u bá»\ufffd chá»\ufffdn máº·c Ä‘á»‹nh (thÆ°á»\ufffdng thÃ¬ nÃªn giá»¯ nguyÃªn logic tÃ¹y báº¡n)
			existingAddress.setIsDefault(false);
		}

//        Cáº­p nháº­t thÃ´ng tin (Mapping dá»¯ liá»‡u má»›i vÃ o cÅ©)
		existingAddress.setReceiverName(addressDetails.getReceiverName());
		existingAddress.setPhone(addressDetails.getPhone());
		existingAddress.setProvince(addressDetails.getProvince());
		existingAddress.setDistrict(addressDetails.getDistrict());
		existingAddress.setWard(addressDetails.getWard());
		existingAddress.setStreet(addressDetails.getStreet());

		existingAddress.setAddressType(addressDetails.getAddressType());

		addressRepository.save(existingAddress);
	}

//     Client
	@Override
	public List<Address> findByUser(User user) {
		return addressRepository.findByUserId(user.getId());
	}

	@Override
	@Transactional
	public void save(Address address) {
//		Náº¿u User chÆ°a cÃ³ Ä‘á»‹a chá»‰ nÃ o -> CÃ¡i Ä‘áº§u tiÃªn auto lÃ  Máº·c Ä‘á»‹nh
		if (address.getUser() != null && addressRepository.countByUserId(address.getUser().getId()) == 0) {
			address.setIsDefault(true);
		}

//		Náº¿u cÃ¡i má»›i lÃ  Máº·c Ä‘á»‹nh -> Reset cÃ¡c cÃ¡i cÅ©
		if (Boolean.TRUE.equals(address.getIsDefault()) && address.getUser() != null) {
			addressRepository.resetDefaultAddresses(address.getUser().getId());
		}
		addressRepository.save(address);
	}

	@Override
	@Transactional
	public void setDefaultAddress(Long addressId, Long userId) {
		// 1. Reset háº¿t thÃ nh false
		addressRepository.resetDefaultAddresses(userId);

		// 2. Set cÃ¡i Ä‘Æ°á»£c chá»\ufffdn thÃ nh true
		Address address = addressRepository.findById(addressId).orElse(null);
		if (address != null && address.getUser().getId().equals(userId)) {
			address.setIsDefault(true);
			addressRepository.save(address);
		}
	}

	@Override
	public Address getDefaultAddress(User user) {
		// Láº¥y táº¥t cáº£ Ä‘á»‹a chá»‰ cá»§a user
		List<Address> addresses = addressRepository.findByUser(user);

		if (addresses.isEmpty()) {
			return null;
		}

		// Logic tÃ¬m cÃ¡i máº·c Ä‘á»‹nh (IsDefault = true)
		// Náº¿u khÃ´ng cÃ³ cÃ¡i nÃ o máº·c Ä‘á»‹nh thÃ¬ láº¥y cÃ¡i Ä‘áº§u tiÃªn
		return addresses.stream().filter(Address::getIsDefault).findFirst().orElse(addresses.get(0));
	}
}