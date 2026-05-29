package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feed_items")
public class FeedItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private String content;
    private String topic;
    private Long likeCount;
    private Long commentCount;
    private Long repostCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
