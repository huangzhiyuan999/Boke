package com.boke.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boke.entity.SiteConfig;
import com.boke.mapper.SiteConfigMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteConfigService extends ServiceImpl<SiteConfigMapper, SiteConfig> {

    public String getValue(String key) {
        return baseMapper.findValueByKey(key);
    }

    public Map<String, String> getAllConfig() {
        List<SiteConfig> configs = list();
        Map<String, String> map = new LinkedHashMap<>();
        for (SiteConfig config : configs) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return map;
    }

    public void setConfig(String key, String value) {
        SiteConfig config = baseMapper.findByKey(key);
        if (config != null) {
            config.setConfigValue(value);
            updateById(config);
        } else {
            config = new SiteConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            save(config);
        }
    }
}
