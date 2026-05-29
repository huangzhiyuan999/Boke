package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("site_visits")
public class SiteVisit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String pagePath;
    private String visitorIp;
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime visitedAt;
}
