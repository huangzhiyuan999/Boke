package com.boke.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("feed_images")
public class FeedImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feedId;
    private String imageUrl;
    private Integer sortOrder;
}
