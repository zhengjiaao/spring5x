package com.zja.util;

import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import static java.util.Objects.isNull;

/**
 * @author ZhengJa
 * @description XMemcached 封装的工具类
 * @data 2019/11/12
 */
public class XMemcachedUtil {

    @Autowired
    private MemcachedClient memcachedClient;

    /**
     * 添加一个键值对到缓存中: 若key存在-结果返回false，默认永久缓存
     * @param key
     * @param value
     * @return boolean 若key存在-结果返回false，key不存在返回true
     */
    public boolean addCache(String key, Object value) throws Exception {
        return addCache(key, value, 0);
    }

    /**
     * 添加一个键值对到缓存中，并设置其超时时间
     * @param key
     * @param value
     * @param expiry 超时时间（单位：分钟），0，即永不过期
     * @return boolean
     */
    public boolean addCache(String key, Object value, int expiry) throws Exception {

        if (StringUtils.isEmpty(key) || value == null) {
            throw new IllegalArgumentException("参数错误！");
        }
        boolean isCache = memcachedClient.add(key, expiry*60, value);

        if (!isCache) {
            throw new IllegalStateException("缓存存储失败！");
        }

        return isCache;
    }

    /**
     * 更新数据：根据key 更新value值
     * @param key
     * @param value 值
     * @param expiry 时间
     * @return boolean 更新结果
     */
    public boolean replaceValue(String key,Object value,int expiry) throws Exception {
        boolean replaceResult =false;
        Object cache = findCache(key);
        if (!isNull(cache)){
            replaceResult=memcachedClient.replace(key,expiry*60,value);
        }
        return replaceResult;
    }

    /**
     * 获取缓存数据：根据key获取 value 值
     * @param key 缓存中的key
     * @return java.lang.Object
     */
    public Object findCache(String key) throws Exception {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("参数错误！");
        }
        return memcachedClient.get(key);
    }

    /**
     * 删除指定缓存：根据key删除
     * @param key 缓存中的key
     * @return void
     */
    public boolean deleteCache(String key) throws Exception {
        boolean deleteResult =false;
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("参数错误！");
        }
        Object cache = findCache(key);
        if (!isNull(cache)){
            deleteResult=memcachedClient.delete(key);
        }
        return deleteResult;
    }

    /**
     * 清空全部缓存 cache ,谨慎使用 真正项目上禁用
     * @param
     * @return void
     */
    public void flushAll() throws Exception {
        memcachedClient.flushAll();
    }
}
