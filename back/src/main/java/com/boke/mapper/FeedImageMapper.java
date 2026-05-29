package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.FeedImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedImageMapper extends BaseMapper<FeedImage> {

    @Select("SELECT * FROM feed_images WHERE feed_id = #{feedId} ORDER BY sort_order")
    List<FeedImage> findByFeedId(@Param("feedId") Long feedId);

    @Select("SELECT image_url FROM feed_images WHERE feed_id = #{feedId} ORDER BY sort_order")
    List<String> findUrlsByFeedId(@Param("feedId") Long feedId);
}
