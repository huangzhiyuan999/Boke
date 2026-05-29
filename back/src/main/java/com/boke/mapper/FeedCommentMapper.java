package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.FeedComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedCommentMapper extends BaseMapper<FeedComment> {

    @Select("SELECT c.*, u.username as author, u.avatar_color as avatarColor " +
            "FROM feed_comments c LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.feed_id = #{feedId} ORDER BY c.created_at ASC")
    List<FeedComment> findByFeedId(@Param("feedId") Long feedId);
}
