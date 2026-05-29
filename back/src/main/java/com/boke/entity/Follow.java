package com.boke.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("follows")
public class Follow {
    private Long followerId;
    private Long followedId;
    private LocalDateTime createdAt;
}
