package com.boke.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminStats {
    private long postCount;
    private long announcementCount;
    private long messageCount;
    private long userCount;
    private long totalVisits;
    private List<ChartData> postTrend;
    private List<ChartData> userTrend;
}
