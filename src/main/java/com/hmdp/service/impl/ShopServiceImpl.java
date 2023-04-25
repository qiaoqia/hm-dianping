package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zero
 * 
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {

        String key = CACHE_SHOP_KEY+id;
        //1从Redis中查询商铺缓存
        String shopJosn = stringRedisTemplate.opsForValue().get(key);
        //2判断是否存在
        if (StrUtil.isNotBlank(shopJosn)) {
            //存在直接返回
            Shop shop = JSONUtil.toBean(shopJosn, Shop.class);
            return Result.ok(shop);
        }
        // 不存在根据ID查询数据库
        Shop shop = getById(id);
        // 不存在返回错误
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        // 存在 写入Redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shop),30L, TimeUnit.MINUTES);
        //返回
        return Result.ok(shop);
    }

    /**
     * 更新数据，先更新后删除
     * @param shop
     * @return
     */
    @Override
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("商铺不存在");
        }
        //更新数据库
        boolean b = updateById(shop);

        //删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
