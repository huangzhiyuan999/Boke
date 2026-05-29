package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.FeedLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeedLikeMapper extends BaseMapper<FeedLike> {

    @Select("SELECT COUNT(*) FROM feed_likes WHERE feed_id = #{feedId} AND user_id = #{userId}")
    int exists(@Param("feedId") Long feedId, @Param("userId") Long userId);

    @Delete("DELETE FROM feed_likes WHERE feed_id = #{feedId} AND user_id = #{userId}")
    int delete(@Param("feedId") Long feedId, @Param("userId") Long userId);

    @Select("SELECT 1 FROM feed_likes WHERE feed_id = #{feedId} AND user_id = #{userId} LIMIT 1")
    Integer isLiked(@Param("feedId") Long feedId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM feed_likes WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
