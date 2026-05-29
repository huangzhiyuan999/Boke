package com.boke.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.dto.LoginRequest;
import com.boke.dto.LoginResponse;
import com.boke.entity.User;
import com.boke.mapper.UserMapper;
import com.boke.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest req) {
        User user = baseMapper.findByUsername(req.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if ("banned".equals(user.getStatus())) {
            throw new RuntimeException("账号已被封禁");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public User register(String username, String password, String email) {
        if (baseMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (email != null && baseMapper.findByEmail(email) != null) {
            throw new RuntimeException("邮箱已注册");
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole("user");
        user.setStatus("active");
        user.setAvatarColor("linear-gradient(135deg, #5B8C5A, #7EC8A8)");
        save(user);
        return user;
    }

    public User getByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("当前密码错误");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    public Page<User> listUsers(long page, long size) {
        Page<User> p = new Page<>(page, size);
        return page(p);
    }
}
