package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT * FROM tags WHERE name = #{name}")
    Tag findByName(@Param("name") String name);

    @Select("SELECT * FROM tags WHERE slug = #{slug}")
    Tag findBySlug(@Param("slug") String slug);

    @Select("SELECT t.* FROM tags t " +
            "INNER JOIN post_tags pt ON t.id = pt.tag_id " +
            "WHERE pt.post_id = #{postId}")
    List<Tag> findTagsByPostId(@Param("postId") Long postId);
}
