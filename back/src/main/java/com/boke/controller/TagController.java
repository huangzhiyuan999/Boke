package com.boke.controller;

import com.boke.common.Result;
import com.boke.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final PostService postService;

    @GetMapping
    public Result<List<String>> allTags() {
        return Result.success(postService.getAllTags());
    }
}
