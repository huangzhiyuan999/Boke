package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entertainment_games")
public class EntertainmentGame {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String shortName;
    private String genre;
    private String description;
    private String tags;
    private String rating;
    private Integer players;
    private String imageUrl;
    private String detailBackground;
    private String demoTitle;
    private String demoText;
    private Integer sortOrder;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
