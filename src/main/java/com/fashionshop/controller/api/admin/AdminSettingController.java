package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Setting;
import com.fashionshop.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/settings")
public class AdminSettingController {

    @Autowired
    private SettingRepository settingRepository;

    @GetMapping
    public ApiResponse<Map<String, String>> getAllSettings() {
        List<Setting> settings = settingRepository.findAll();
        Map<String, String> settingsMap = settings.stream()
                .collect(Collectors.toMap(Setting::getSettingKey, Setting::getSettingValue));
        return ApiResponse.success(settingsMap);
    }

    @PostMapping
    public ApiResponse<String> saveSettings(@RequestBody Map<String, String> payload) {
        payload.forEach((key, value) -> {
            Setting setting = settingRepository.findBySettingKey(key).orElse(new Setting());
            setting.setSettingKey(key);
            setting.setSettingValue(value);
            settingRepository.save(setting);
        });
        return ApiResponse.success("Cập nhật cài đặt thành công");
    }
}
