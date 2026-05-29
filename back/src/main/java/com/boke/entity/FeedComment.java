package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feed_comments")
public class FeedComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feedId;
    private Long userId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private String author;

    @TableField(exist = false)
    private String avatarColor;
}
