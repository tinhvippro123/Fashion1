package com.fashionshop.service.impl;

import com.fashionshop.model.LoginHistory;
import com.fashionshop.model.User;
import com.fashionshop.repository.LoginHistoryRepository;
import com.fashionshop.service.LoginHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Override
    public void saveLoginHistory(User user, HttpServletRequest request) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        
        // Parsing IP Address
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // In case of multiple proxies, X-Forwarded-For contains comma separated IPs
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        history.setIpAddress(ipAddress);

        // Parsing User-Agent for Device
        String userAgent = request.getHeader("User-Agent");
        String device = "Khác";
        if (userAgent != null) {
            String ua = userAgent.toLowerCase();
            if (ua.contains("windows")) {
                device = "Windows (desktop)";
            } else if (ua.contains("mac")) {
                device = "MacOS";
            } else if (ua.contains("iphone") || ua.contains("ipad")) {
                device = "iOS";
            } else if (ua.contains("android")) {
                device = "Android";
            } else if (ua.contains("linux")) {
                device = "Linux";
            }
        }
        history.setDevice(device);
        
        // Hardcoded software as requested by the look of the site
        history.setSoftware("Website luxefashion.com");
        history.setLoginType("Mặc định");

        loginHistoryRepository.save(history);
    }

    @Override
    public List<LoginHistory> getLoginHistoryByUser(User user, int limit) {
        return loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(user.getId(), PageRequest.of(0, limit));
    }
}
