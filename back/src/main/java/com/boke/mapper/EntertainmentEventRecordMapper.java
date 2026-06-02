package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentEventRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EntertainmentEventRecordMapper extends BaseMapper<EntertainmentEventRecord> {
    @Select("SELECT event_id FROM entertainment_event_records WHERE user_id = #{userId}")
    List<Long> findCompletedEventIds(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM entertainment_event_records WHERE user_id = #{userId} AND event_id = #{eventId}")
    int existsRecord(@Param("userId") Long userId, @Param("eventId") Long eventId);
}
