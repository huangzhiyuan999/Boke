package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entertainment_events")
public class EntertainmentEvent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String icon;
    private String description;
    private Integer reward;
    private Integer defaultDone;
    private Integer sortOrder;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
