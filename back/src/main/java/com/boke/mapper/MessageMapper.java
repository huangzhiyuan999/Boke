package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT m.*, COALESCE(u.username, m.guest_name) as guest_name " +
            "FROM messages m LEFT JOIN users u ON m.user_id = u.id " +
            "WHERE m.is_visible = 1 ORDER BY m.created_at DESC")
    List<Message> findVisibleMessages();
}
