package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entertainment_event_records")
public class EntertainmentEventRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDateTime completedAt;
}
