package com.fashionshop.service.impl;

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
		return addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
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
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		address.setUser(user);

		save(address);
	}

	@Override
	@Transactional
	public void updateAddress(Long userId, Address addressDetails) {
//      Lấy ID từ chính object form gửi lên (do có input hidden name="id")
		Long addressId = addressDetails.getId();

//      Tìm địa chỉ cũ trong DB
		Address existingAddress = addressRepository.findById(addressId)
				.orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));

//         Kiểm tra bảo mật:
//         Người đang đăng nhập (userId) có phải là chủ của địa chỉ này không?
		if (!existingAddress.getUser().getId().equals(userId)) {
			throw new RuntimeException("Bạn không có quyền sửa địa chỉ này!");
		}

//      Logic xử lý Mặc định
//      Nếu user chọn cái này là mặc định -> Reset hết các cái khác
		if (Boolean.TRUE.equals(addressDetails.getIsDefault())) {
			addressRepository.resetDefaultAddresses(userId);
			existingAddress.setIsDefault(true);
		} else {
			// Nếu bỏ chọn mặc định (thường thì nên giữ nguyên logic tùy bạn)
			existingAddress.setIsDefault(false);
		}

//        Cập nhật thông tin (Mapping dữ liệu mới vào cũ)
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
//		Nếu User chưa có địa chỉ nào -> Cái đầu tiên auto là Mặc định
		if (address.getUser() != null && addressRepository.countByUserId(address.getUser().getId()) == 0) {
			address.setIsDefault(true);
		}

//		Nếu cái mới là Mặc định -> Reset các cái cũ
		if (Boolean.TRUE.equals(address.getIsDefault()) && address.getUser() != null) {
			addressRepository.resetDefaultAddresses(address.getUser().getId());
		}
		addressRepository.save(address);
	}

	@Override
	@Transactional
	public void setDefaultAddress(Long addressId, Long userId) {
		// 1. Reset hết thành false
		addressRepository.resetDefaultAddresses(userId);

		// 2. Set cái được chọn thành true
		Address address = addressRepository.findById(addressId).orElse(null);
		if (address != null && address.getUser().getId().equals(userId)) {
			address.setIsDefault(true);
			addressRepository.save(address);
		}
	}

	@Override
	public Address getDefaultAddress(User user) {
		// Lấy tất cả địa chỉ của user
		List<Address> addresses = addressRepository.findByUser(user);

		if (addresses.isEmpty()) {
			return null;
		}

		// Logic tìm cái mặc định (IsDefault = true)
		// Nếu không có cái nào mặc định thì lấy cái đầu tiên
		return addresses.stream().filter(Address::getIsDefault).findFirst().orElse(addresses.get(0));
	}
}