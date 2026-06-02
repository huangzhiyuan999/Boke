package com.boke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntertainmentItemVO {
    private Long id;
    private String name;
    private String category;
    private String desc;
    private Integer price;
    private String badge;
    private Integer stock;
    private Integer sales;
    private String image;
}
