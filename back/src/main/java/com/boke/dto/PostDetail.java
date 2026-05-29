package com.boke.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostDetail {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String title;
    private String summary;
    private String cover;
    private String content;
    private String postType;
    private Long viewCount;
    private Integer isPublished;
    private List<String> tags;
    private String createdAt;
    private String updatedAt;
}
