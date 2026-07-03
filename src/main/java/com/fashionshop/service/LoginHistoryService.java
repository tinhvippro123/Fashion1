package com.fashionshop.service;

import com.fashionshop.model.LoginHistory;
import com.fashionshop.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface LoginHistoryService {
    void saveLoginHistory(User user, HttpServletRequest request);
    List<LoginHistory> getLoginHistoryByUser(User user, int limit);
}
