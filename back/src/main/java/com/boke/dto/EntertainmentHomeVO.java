package com.boke.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EntertainmentHomeVO {
    private EntertainmentWalletVO wallet;
    private List<EntertainmentItemVO> items;
    private List<EntertainmentGameVO> games;
    private List<EntertainmentEventVO> events;
}
