package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.ChangePasswordRequest;
import com.boke.dto.LoginRequest;
import com.boke.dto.LoginResponse;
import com.boke.entity.User;
import com.boke.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse resp = userService.login(req);
            return Result.success(resp);
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body) {
        try {
            User user = userService.register(
                    body.get("username"),
                    body.get("password"),
                    body.get("email")
            );
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/me")
    public Result<User> me(Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        User user = userService.getById(userId);
        user.setPasswordHash(null);
        return Result.success(user);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest req, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        try {
            Long userId = (Long) auth.getPrincipal();
            userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
