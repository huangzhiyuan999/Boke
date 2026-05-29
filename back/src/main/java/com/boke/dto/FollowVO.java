package com.boke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowVO {
    private Long id;
    private String name;
    private String avatarColor;
    private String bio;
    private String lastPost;
    private Long newPosts;
    private Boolean followed;
    private String fans;
}
