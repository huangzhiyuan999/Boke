package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentGameRank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EntertainmentGameRankMapper extends BaseMapper<EntertainmentGameRank> {
    @Select("SELECT * FROM entertainment_game_ranks WHERE game_id = #{gameId} ORDER BY score DESC, sort_order ASC LIMIT 10")
    List<EntertainmentGameRank> findTopByGameId(@Param("gameId") Long gameId);
}
