package com.boke.controller;

import com.boke.common.Result;
import com.boke.service.SiteConfigService;
import com.boke.service.SiteVisitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService configService;
    private final SiteVisitService visitService;

    @GetMapping("/config")
    public Result<Map<String, String>> getConfig() {
        return Result.success(configService.getAllConfig());
    }

    @PutMapping("/config")
    public Result<Void> updateConfig(@RequestBody Map<String, String> body, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        body.forEach(configService::setConfig);
        return Result.success();
    }

    @PostMapping("/visits")
    public Result<Void> recordVisit(@RequestBody Map<String, String> body, HttpServletRequest request, Authentication auth) {
        String pagePath = body.getOrDefault("pagePath", "/");
        String ip = request.getRemoteAddr();
        Long userId = auth != null ? (Long) auth.getPrincipal() : null;
        visitService.recordVisit(pagePath, ip, userId);
        return Result.success();
    }
}
