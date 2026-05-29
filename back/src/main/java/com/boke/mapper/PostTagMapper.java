package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.PostTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostTagMapper extends BaseMapper<PostTag> {

    @Delete("DELETE FROM post_tags WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Long postId);

    @Select("SELECT DISTINCT p.* FROM posts p " +
            "INNER JOIN post_tags pt ON p.id = pt.post_id " +
            "INNER JOIN tags t ON t.id = pt.tag_id " +
            "WHERE t.name = #{tagName} AND p.is_published = 1 " +
            "ORDER BY p.created_at DESC")
    List<PostTag> findPostsByTagName(@Param("tagName") String tagName);
}
