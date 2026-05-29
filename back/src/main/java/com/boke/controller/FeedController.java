package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.CommentRequest;
import com.boke.dto.FeedRequest;
import com.boke.dto.FeedVO;
import com.boke.entity.FeedComment;
import com.boke.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public Result<List<FeedVO>> list(Authentication auth) {
        Long userId = auth != null ? (Long) auth.getPrincipal() : null;
        return Result.success(feedService.getFeeds(userId));
    }

    @GetMapping("/following")
    public Result<List<FeedVO>> following(Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(feedService.getFollowingFeeds(userId));
    }

    @PostMapping
    public Result<FeedVO> create(@RequestBody FeedRequest req, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(feedService.createFeed(req, userId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        try {
            feedService.deleteFeed(id, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }

    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        feedService.likeFeed(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        feedService.unlikeFeed(id, userId);
        return Result.success();
    }

    @GetMapping("/{id}/comments")
    public Result<List<FeedComment>> comments(@PathVariable Long id) {
        return Result.success(feedService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public Result<FeedComment> addComment(@PathVariable Long id, @RequestBody CommentRequest req, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(feedService.addComment(id, userId, req.getContent()));
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        try {
            feedService.deleteComment(commentId, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }
}
