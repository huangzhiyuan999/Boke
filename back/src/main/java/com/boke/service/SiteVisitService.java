package com.boke.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.entity.SiteVisit;
import com.boke.mapper.SiteVisitMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteVisitService extends ServiceImpl<SiteVisitMapper, SiteVisit> {

    public void recordVisit(String pagePath, String visitorIp, Long userId) {
        SiteVisit visit = new SiteVisit();
        visit.setPagePath(pagePath);
        visit.setVisitorIp(visitorIp);
        visit.setUserId(userId);
        save(visit);
    }

    public long getTotalVisits() {
        return baseMapper.countAll();
    }

    public Map<String, Integer> getRecent7DayTrend() {
        LocalDate today = LocalDate.now();
        LocalDateTime since = today.minusDays(6).atStartOfDay();

        List<Map<String, Object>> rows = baseMapper.countByDay(since);

        Map<String, Integer> trend = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            trend.put(date.toString(), 0);
        }

        for (Map<String, Object> row : rows) {
            String dateStr = row.get("visit_date").toString();
            Integer cnt = ((Number) row.get("cnt")).intValue();
            trend.put(dateStr, cnt);
        }

        return trend;
    }
}
