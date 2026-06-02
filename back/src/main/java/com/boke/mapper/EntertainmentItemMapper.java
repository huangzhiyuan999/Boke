package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.EntertainmentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EntertainmentItemMapper extends BaseMapper<EntertainmentItem> {
    @Select("SELECT * FROM entertainment_items WHERE status = 'active' ORDER BY sort_order ASC, id ASC")
    List<EntertainmentItem> findActiveItems();
}
