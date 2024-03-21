package com.sky.service.impl;

import com.sky.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void updateStatus(Integer status) {
        redisTemplate.opsForValue().set("SHOP_STATUS", status);
    }

    @Override
    public Integer selectStatus() {
        return (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
    }


}
