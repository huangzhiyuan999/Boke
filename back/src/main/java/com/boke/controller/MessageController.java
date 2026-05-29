package com.boke.controller;

import com.boke.common.Result;
import com.boke.dto.MessageRequest;
import com.boke.dto.MessageVO;
import com.boke.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Result<List<MessageVO>> list() {
        return Result.success(messageService.getVisibleMessages());
    }

    @PostMapping
    public Result<MessageVO> create(@RequestBody MessageRequest req, Authentication auth) {
        Long userId = auth != null ? (Long) auth.getPrincipal() : null;
        return Result.success(messageService.createMessage(userId, req.getGuestName(), req.getContent()));
    }

    @PutMapping("/{id}/visibility")
    public Result<Void> setVisibility(@PathVariable Long id, @RequestBody Map<String, Boolean> body, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        messageService.setVisibility(id, body.get("visible"));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) return Result.error(401, "未登录");
        messageService.removeById(id);
        return Result.success();
    }
}
