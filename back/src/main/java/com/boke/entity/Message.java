package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String guestName;
    private String content;
    private Integer isVisible;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
