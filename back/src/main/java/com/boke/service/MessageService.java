package com.boke.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.dto.MessageVO;
import com.boke.entity.Message;
import com.boke.entity.User;
import com.boke.mapper.MessageMapper;
import com.boke.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    private final UserMapper userMapper;

    public List<MessageVO> getVisibleMessages() {
        List<Message> messages = baseMapper.findVisibleMessages();
        return messages.stream().map(this::toVO).collect(Collectors.toList());
    }

    public MessageVO createMessage(Long userId, String guestName, String content) {
        Message msg = new Message();
        msg.setUserId(userId);
        msg.setGuestName(guestName != null ? guestName : "匿名");
        msg.setContent(content);
        msg.setIsVisible(1);
        save(msg);
        return toVO(msg);
    }

    public void setVisibility(Long id, boolean visible) {
        Message msg = getById(id);
        if (msg == null) throw new RuntimeException("留言不存在");
        msg.setIsVisible(visible ? 1 : 0);
        updateById(msg);
    }

    private MessageVO toVO(Message msg) {
        String name = msg.getGuestName();
        if (msg.getUserId() != null) {
            User user = userMapper.selectById(msg.getUserId());
            if (user != null) name = user.getUsername();
        }
        String time = msg.getCreatedAt() != null
                ? msg.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : "";
        return new MessageVO(msg.getId(), name, msg.getContent(), time, msg.getIsVisible());
    }
}
