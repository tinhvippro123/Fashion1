package com.fashionshop.config;

import com.fashionshop.enums.UserRole;
import com.fashionshop.model.User;
import com.fashionshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@gmail.com") == null) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPasswordHash(passwordEncoder.encode("123456"));
            admin.setFullName("Administrator");
            admin.setRole(UserRole.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("✅ Đã tạo tài khoản Admin mặc định: admin@gmail.com / 123456");
        }

        if (userRepository.findByEmail("user@gmail.com") == null) {
            User user = new User();
            user.setEmail("user@gmail.com");
            user.setPasswordHash(passwordEncoder.encode("123456"));
            user.setFullName("Customer");
            user.setRole(UserRole.CUSTOMER);
            user.setIsActive(true);
            userRepository.save(user);
            System.out.println("✅ Đã tạo tài khoản User mặc định: user@gmail.com / 123456");
        }
    }
}
