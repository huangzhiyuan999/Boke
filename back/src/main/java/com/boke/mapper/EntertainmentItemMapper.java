package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EntertainmentItemMapper extends BaseMapper<EntertainmentItem> {
    @Select("SELECT * FROM entertainment_items WHERE status = 'active' ORDER BY sort_order ASC, id ASC")
    List<EntertainmentItem> findActiveItems();

    @Update("UPDATE entertainment_items SET stock = stock - 1, wish_count = wish_count + 1, updated_at = NOW() WHERE id = #{id} AND stock > 0")
    int deductStock(@Param("id") Long id);
}
