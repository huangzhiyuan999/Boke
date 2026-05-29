package com.boke.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedVO {
    private Long id;
    private Long authorId;
    private Long userId;
    private String author;
    private String avatarColor;
    private String time;
    private String content;
    private List<String> images;
    private String topic;
    private Long topicCount;
    private Long likes;
    private Long comments;
    private Long reposts;
    private Boolean liked;
}
