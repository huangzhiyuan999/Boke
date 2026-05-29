package com.boke.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boke.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SiteConfigMapper extends BaseMapper<SiteConfig> {

    @Select("SELECT config_value FROM site_config WHERE config_key = #{key}")
    String findValueByKey(@Param("key") String key);

    @Select("SELECT * FROM site_config WHERE config_key = #{key}")
    SiteConfig findByKey(@Param("key") String key);
}
