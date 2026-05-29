package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.SiteVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SiteVisitMapper extends BaseMapper<SiteVisit> {

    @Select("SELECT COUNT(*) FROM site_visits")
    long countAll();

    @Select("SELECT DATE(visited_at) as visit_date, COUNT(*) as cnt " +
            "FROM site_visits " +
            "WHERE visited_at >= #{since} " +
            "GROUP BY DATE(visited_at) ORDER BY visit_date")
    List<java.util.Map<String, Object>> countByDay(@Param("since") LocalDateTime since);
}
