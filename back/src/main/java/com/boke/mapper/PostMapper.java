package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Select("SELECT p.*, u.username as author_name, u.avatar_color as author_avatar " +
            "FROM posts p LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.id = #{id}")
    Post findByIdWithAuthor(@Param("id") Long id);

    @Select("SELECT p.*, u.username as author_name, u.avatar_color as author_avatar " +
            "FROM posts p LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.post_type = #{postType} AND p.is_published = 1 " +
            "ORDER BY p.created_at DESC")
    List<Post> findPublishedByType(@Param("postType") String postType);

    @Select("SELECT p.*, u.username as author_name, u.avatar_color as author_avatar " +
            "FROM posts p LEFT JOIN users u ON p.author_id = u.id " +
            "WHERE p.is_published = 1 " +
            "ORDER BY p.created_at DESC")
    List<Post> findAllPublished();

    @Select("SELECT DISTINCT t.name FROM tags t " +
            "INNER JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = #{postId}")
    List<String> findTagsByPostId(@Param("postId") Long postId);
}
