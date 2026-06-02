package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entertainment_game_plays")
public class EntertainmentGamePlay {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long gameId;
    private Integer score;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
