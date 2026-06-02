package com.boke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntertainmentEventVO {
    private Long id;
    private String icon;
    private String title;
    private String desc;
    private Integer reward;
    private Boolean done;
}
