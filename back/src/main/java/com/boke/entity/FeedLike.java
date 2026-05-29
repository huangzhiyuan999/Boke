package com.boke.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feed_likes")
public class FeedLike {
    private Long feedId;
    private Long userId;
    private LocalDateTime createdAt;
}
