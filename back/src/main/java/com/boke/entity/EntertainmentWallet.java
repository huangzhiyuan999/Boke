package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("entertainment_wallets")
public class EntertainmentWallet {
    @TableId(type = IdType.INPUT)
    private Long userId;
    private Integer coins;
    private LocalDate checkedInDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
