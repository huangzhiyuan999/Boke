package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentGame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EntertainmentGameMapper extends BaseMapper<EntertainmentGame> {
    @Select("SELECT * FROM entertainment_games WHERE status = 'active' ORDER BY sort_order ASC, id ASC")
    List<EntertainmentGame> findActiveGames();

    @Select("SELECT * FROM entertainment_games WHERE code = #{code} AND status = 'active' LIMIT 1")
    EntertainmentGame findActiveByCode(@Param("code") String code);

    @Select("SELECT * FROM entertainment_games WHERE code = #{code} LIMIT 1")
    EntertainmentGame findByCode(@Param("code") String code);

    @Update("UPDATE entertainment_games SET status = 'offline' WHERE code NOT IN ('snake', 'reaction')")
    int offlineUnlistedGames();
}
