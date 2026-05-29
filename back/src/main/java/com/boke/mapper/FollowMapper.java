package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.Follow;
import com.boke.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    @Delete("DELETE FROM follows WHERE follower_id = #{followerId} AND followed_id = #{followedId}")
    int unfollow(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Select("SELECT 1 FROM follows WHERE follower_id = #{followerId} AND followed_id = #{followedId} LIMIT 1")
    Integer isFollowing(@Param("followerId") Long followerId, @Param("followedId") Long followedId);

    @Select("SELECT u.id, u.username, u.avatar_color, u.bio FROM users u " +
            "INNER JOIN follows f ON u.id = f.followed_id " +
            "WHERE f.follower_id = #{userId}")
    List<User> findFollowingUsers(@Param("userId") Long userId);

    @Select("SELECT u.id, u.username, u.avatar_color, u.bio FROM users u " +
            "INNER JOIN follows f ON u.id = f.follower_id " +
            "WHERE f.followed_id = #{userId}")
    List<User> findFollowerUsers(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM follows WHERE followed_id = #{userId}")
    int countFollowers(@Param("userId") Long userId);
}
