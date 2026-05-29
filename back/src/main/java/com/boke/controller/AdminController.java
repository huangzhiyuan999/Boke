package com.boke.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boke.common.PageResult;
import com.boke.common.Result;
import com.boke.dto.AdminStats;
import com.boke.dto.ChartData;
import com.boke.entity.User;
import com.boke.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PostService postService;
    private final MessageService messageService;
    private final UserService userService;
    private final SiteVisitService visitService;

    @GetMapping("/stats")
    public Result<AdminStats> stats() {
        AdminStats stats = new AdminStats();
        stats.setPostCount(postService.lambdaQuery()
                .eq(com.boke.entity.Post::getPostType, "post").count());
        stats.setAnnouncementCount(postService.lambdaQuery()
                .eq(com.boke.entity.Post::getPostType, "announcement").count());
        stats.setMessageCount(messageService.count());
        stats.setUserCount(userService.count());
        stats.setTotalVisits(visitService.getTotalVisits());

        // 7-day trend (simplified: static data)
        List<String> labels = new ArrayList<>();
        List<Integer> postData = new ArrayList<>();
        List<Integer> userData = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            labels.add(today.minusDays(i).toString().substring(5));
            postData.add((int) (Math.random() * 400) + 100);
            userData.add((int) (Math.random() * 16) + 2);
        }

        List<ChartData> postTrend = List.of(
                new ChartData("文章页", "#5B8C5A", postData),
                new ChartData("推荐页", "#f59e0b", postData.stream().map(v -> (int)(v * 0.6)).toList())
        );
        List<ChartData> userTrend = List.of(
                new ChartData("留言数", "#3b82f6", userData),
                new ChartData("新注册", "#8b5cf6", userData.stream().map(v -> (int)(v * 0.4)).toList())
        );

        stats.setPostTrend(postTrend);
        stats.setUserTrend(userTrend);
        return Result.success(stats);
    }

    @GetMapping("/users")
    public Result<PageResult<User>> users(@RequestParam(defaultValue = "1") long page,
                                           @RequestParam(defaultValue = "10") long size) {
        Page<User> p = userService.listUsers(page, size);
        p.getRecords().forEach(u -> u.setPasswordHash(null));
        return Result.success(PageResult.of(p.getRecords(), p.getTotal(), page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userService.getById(id);
        if (user == null) return Result.error(404, "用户不存在");
        user.setStatus(body.get("status"));
        userService.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) return Result.error(404, "用户不存在");
        if ("admin".equals(user.getRole())) return Result.error(403, "不能删除管理员");
        userService.removeById(id);
        return Result.success();
    }
}
