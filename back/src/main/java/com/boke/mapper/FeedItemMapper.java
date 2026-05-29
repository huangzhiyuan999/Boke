package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.FeedItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeedItemMapper extends BaseMapper<FeedItem> {

    @Select("SELECT f.*, u.username as author_name, u.avatar_color as author_avatar " +
            "FROM feed_items f LEFT JOIN users u ON f.author_id = u.id " +
            "ORDER BY f.created_at DESC")
    List<FeedItem> findAllWithAuthor();

    @Select("SELECT f.*, u.username as author_name, u.avatar_color as author_avatar " +
            "FROM feed_items f LEFT JOIN users u ON f.author_id = u.id " +
            "WHERE f.author_id IN (" +
            "  SELECT followed_id FROM follows WHERE follower_id = #{userId}" +
            ") ORDER BY f.created_at DESC")
    List<FeedItem> findFollowingFeeds(@Param("userId") Long userId);

    @Update("UPDATE feed_items SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE feed_items SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    int decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE feed_items SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(@Param("id") Long id);

    @Update("UPDATE feed_items SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{id}")
    int decrementCommentCount(@Param("id") Long id);
}
