package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.*;
import com.boke.service.EntertainmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entertainment")
@RequiredArgsConstructor
public class EntertainmentController {

    private final EntertainmentService entertainmentService;

    @GetMapping
    public Result<EntertainmentHomeVO> home(Authentication auth) {
        return Result.success(entertainmentService.getHome(currentUserId(auth)));
    }

    @GetMapping("/items")
    public Result<List<EntertainmentItemVO>> items() {
        return Result.success(entertainmentService.getItems());
    }

    @GetMapping("/games")
    public Result<List<EntertainmentGameVO>> games() {
        return Result.success(entertainmentService.getGames());
    }

    @GetMapping("/games/{code}")
    public Result<EntertainmentGameVO> game(@PathVariable String code) {
        return Result.success(entertainmentService.getGame(code));
    }

    @GetMapping("/events")
    public Result<List<EntertainmentEventVO>> events(Authentication auth) {
        return Result.success(entertainmentService.getEvents(currentUserId(auth)));
    }

    @GetMapping("/wallet")
    public Result<EntertainmentWalletVO> wallet(Authentication auth) {
        Long userId = currentUserId(auth);
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(entertainmentService.getWallet(userId));
    }

    @PostMapping("/check-in")
    public Result<EntertainmentWalletVO> checkIn(Authentication auth) {
        Long userId = currentUserId(auth);
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(entertainmentService.checkIn(userId));
    }

    @PostMapping("/events/{id}/finish")
    public Result<EntertainmentWalletVO> finishEvent(@PathVariable Long id, Authentication auth) {
        Long userId = currentUserId(auth);
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(entertainmentService.finishEvent(userId, id));
    }

    @PostMapping("/games/{code}/play")
    public Result<EntertainmentWalletVO> playGame(@PathVariable String code,
                                                  @RequestBody(required = false) GamePlayRequest request,
                                                  Authentication auth) {
        Integer score = request != null ? request.getScore() : 0;
        return Result.success(entertainmentService.playGame(currentUserId(auth), code, score));
    }

    @PostMapping("/items/{id}/buy")
    public Result<EntertainmentWalletVO> buyItem(@PathVariable Long id, Authentication auth) {
        Long userId = currentUserId(auth);
        if (userId == null) return Result.error(401, "未登录");
        return Result.success(entertainmentService.buyItem(userId, id));
    }

    private Long currentUserId(Authentication auth) {
        return auth != null ? (Long) auth.getPrincipal() : null;
    }
}
