package com.boke.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String summary;
    private String cover;
    private String content;
    private String postType;
    private Integer isPublished;
    private Long viewCount;
    private java.util.List<String> tags;
}
