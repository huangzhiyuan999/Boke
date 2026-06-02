package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EntertainmentEventMapper extends BaseMapper<EntertainmentEvent> {
    @Select("SELECT * FROM entertainment_events WHERE status = 'active' ORDER BY sort_order ASC, id ASC")
    List<EntertainmentEvent> findActiveEvents();
}
