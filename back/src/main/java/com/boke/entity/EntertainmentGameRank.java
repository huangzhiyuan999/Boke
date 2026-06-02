package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("entertainment_game_ranks")
public class EntertainmentGameRank {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gameId;
    private String playerName;
    private Integer score;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
