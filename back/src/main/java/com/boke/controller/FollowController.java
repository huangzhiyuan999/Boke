package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.FollowVO;
import com.boke.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public Result<Void> follow(@RequestBody Map<String, Long> body, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        try {
            followService.follow(userId, body.get("followedId"));
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{followedId}")
    public Result<Void> unfollow(@PathVariable Long followedId, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        followService.unfollow(userId, followedId);
        return Result.success();
    }

    @GetMapping("/following")
    public Result<List<FollowVO>> following(Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(followService.getFollowing(userId));
    }

    @GetMapping("/followers")
    public Result<List<FollowVO>> followers(Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(followService.getFollowers(userId));
    }

    @GetMapping("/suggestions")
    public Result<List<FollowVO>> suggestions(Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(followService.getSuggestions(userId));
    }

    @GetMapping("/check/{followedId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long followedId, Authentication auth) {
        if (auth == null) return Result.success(Map.of("following", false));
        Long userId = (Long) auth.getPrincipal();
        List<FollowVO> following = followService.getFollowing(userId);
        boolean isFollowing = following.stream().anyMatch(f -> f.getId().equals(followedId));
        return Result.success(Map.of("following", isFollowing));
    }

    @GetMapping("/search")
    public Result<List<FollowVO>> search(@RequestParam String q, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(followService.searchUsers(userId, q));
    }
}
