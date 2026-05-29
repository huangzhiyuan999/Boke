package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.PostDetail;
import com.boke.dto.PostRequest;
import com.boke.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public Result<List<PostDetail>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sort) {
        if (tag != null && !tag.isEmpty()) {
            return Result.success(postService.getPostsByTag(tag));
        }
        if ("hot".equals(sort)) {
            return Result.success(postService.getHotPosts());
        }
        if ("announcement".equals(type)) {
            return Result.success(postService.getPublishedByType("announcement"));
        }
        if ("post".equals(type)) {
            return Result.success(postService.getPublishedByType("post"));
        }
        return Result.success(postService.getPublishedPosts());
    }

    @GetMapping("/{id}")
    public Result<PostDetail> detail(@PathVariable Long id) {
        PostDetail detail = postService.getPostDetail(id);
        if (detail == null) return Result.error(404, "文章不存在");
        return Result.success(detail);
    }

    @PostMapping
    public Result<PostDetail> create(@RequestBody PostRequest req, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        Long userId = (Long) auth.getPrincipal();
        return Result.success(postService.createPost(req, userId));
    }

    @PutMapping("/{id}")
    public Result<PostDetail> update(@PathVariable Long id, @RequestBody PostRequest req, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        return Result.success(postService.updatePost(id, req));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        postService.deletePost(id);
        return Result.success();
    }
}
