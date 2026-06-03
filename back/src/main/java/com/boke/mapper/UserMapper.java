package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Update("UPDATE users SET balance = balance + #{amount}, updated_at = NOW() WHERE id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") Integer amount);

    @Update("UPDATE users SET balance = balance - #{amount}, updated_at = NOW() WHERE id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") Integer amount);
}
