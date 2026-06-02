package com.boke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EntertainmentGameVO {
    private Long id;
    private String code;
    private String name;
    private String shortName;
    private String genre;
    private String desc;
    private List<String> tags;
    private String rating;
    private Integer players;
    private String image;
    private String detailBg;
    private String demoTitle;
    private String demoText;
    private List<EntertainmentRankVO> ranks;
}
