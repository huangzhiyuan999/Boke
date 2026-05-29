package com.boke.dto;

import java.util.List;

import lombok.Data;

@Data
public class FeedRequest {
    private String content;
    private String topic;
    private List<String> images;
}
