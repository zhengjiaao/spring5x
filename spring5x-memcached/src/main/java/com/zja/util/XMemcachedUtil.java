package com.zja.util;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeoutException;

import static java.util.Objects.isNull;

/**
 * @author ZhengJa
 * @description XMemcached 封装的工具类：说明，如果对XMemcachedClient有了解，可以使用MemcachedClient本身封装的方法
 * @data 2019/11/12
 */
public class XMemcachedUtil {

    @Autowired
    private MemcachedClient memcachedClient;



    /*=================【1、更新缓存失效时间】===================*/

    /**
     * 指定更新缓存失效时间
     *
     * @param key  键
     * @param expiry 存储有效时间（秒） 0-永久存储
     * @return boolean  true/false --> 成功/失败
     */
    public boolean expire(String key, int expiry) throws InterruptedException, MemcachedException, TimeoutException {
        boolean touchResult = false;
        if (expiry > 0) {
            touchResult = memcachedClient.touch(key, expiry);
        }
        return touchResult;
    }


    /*=================【2、定义新增set/add方法】===================*/

    /** add: 当内存中已经有key存在，则会添加失败。如果内存中key不存在，则可以添加成功
     *
     * 添加一个键值对到缓存中:时间不过期
     * @param key   键
     * @param value 值
     * @return boolean 若key存在-结果返回false，key不存在返回true
     */
    public boolean add(String key, Object value) throws InterruptedException, MemcachedException, TimeoutException {
        return add(key, value, 0);
    }

    /** add: 当内存中已经有key存在，则会添加失败。如果内存中key不存在，则可以添加成功
     *
     * 添加一个键值对到缓存中，并设置其超时时间
     *
     * @param key    键
     * @param value  值
     * @param expiry 存储有效时间（秒） 0-永久存储
     * @return boolean 若key存在-结果返回false
     */
    public boolean add(String key, Object value, int expiry) throws InterruptedException, MemcachedException, TimeoutException {
        if (StringUtils.isEmpty(key) || value == null) {
            throw new IllegalArgumentException("参数错误！");
        }
        return memcachedClient.add(key, expiry, value);
    }

    /** set: 重复的set操作，会覆盖之前的值
     *
     * 添加一个键值对到缓存中:永久存储 ,与and区别使：若key存在-更新value值
     * @param key   键
     * @param value 值
     */
    public boolean set(String key, Object value) throws InterruptedException, MemcachedException, TimeoutException {
        return set(key, value, 0);
    }

    /** set: 重复的set操作，会覆盖之前的值
     *
     * 添加一个键值对到缓存中，与and区别使：若key存在-更新value值
     * @param key    键
     * @param value  值
     * @param expiry 存储有效时间（秒） 0-永久存储
     */
    public boolean set(String key, Object value, int expiry) throws InterruptedException, MemcachedException, TimeoutException {
        if (StringUtils.isEmpty(key) || value == null) {
            throw new IllegalArgumentException("参数错误！");
        }
        return memcachedClient.set(key, expiry, value);
    }



    /*=================【3、定义更新replace方法】===================*/

    /**
     * 更新数据：根据key 更新value值,时间不过期
     *
     * @param key   键
     * @param value 值
     * @return boolean 更新结果
     */
    public boolean replace(String key, Object value) throws InterruptedException, MemcachedException, TimeoutException {
        return replace(key, value, 0);
    }

    /**
     * 更新数据：根据key 更新value值
     *
     * @param key    键
     * @param value  值
     * @param expiry 存储有效时间（秒） 0-永久存储
     * @return boolean 更新结果
     */
    public boolean replace(String key, Object value, int expiry) throws InterruptedException, MemcachedException, TimeoutException {
        boolean replaceResult = false;
        Object cache = get(key);
        if (!isNull(cache)) {
            replaceResult = memcachedClient.replace(key, expiry, value);
        }
        return replaceResult;
    }


    /*=================【4、定义查询get/gets方法】===================*/
    /*gets对比get：gets返回的信息稍微多一些*/

    /**get: 获取一个数据，如果写入时是压缩的或序列化的，则get的返回会自动解压缩及反序列化
     * 获取缓存数据：根据key获取 value 值
     * @param key 缓存中的key
     */
    public <T> T get(String key) throws InterruptedException, MemcachedException, TimeoutException {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("参数错误！");
        }
        return memcachedClient.get(key);
    }

    /**
     * 获取缓存数据并更新超时时间
     *
     * @param key  键
     * @param time 存储有效时间（秒） 0-永久存储
     * @return boolean  true/false --> 成功/失败
     */
    public boolean getAndTouch(String key, int time) throws InterruptedException, MemcachedException, TimeoutException {
        boolean touchResult = false;
        if (time > 0) {
            touchResult = memcachedClient.getAndTouch(key, time);
        }
        return touchResult;
    }



    /**gets : gets除了会返回缓存值外，还会返回当前缓存的版本号，一般是用于协同CAS完成原子操作使用
     * 获取缓存数据：根据key获取 value 值
     * @param key 缓存中的key
     */
    public <T> GetsResponse<T> gets(String key) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.gets(key);
    }



    /*=================【5、定义删除delete方法】===================*/

    /**
     * 删除指定缓存：根据key删除
     *
     * @param key 缓存中的key
     */
    public boolean delete(String key) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.delete(key);
    }

    /**
     * 删除数据: 通过deleteWithNoReply方法，删除数据并且告诉memcached,不用返回应答，因此这个方法不会等待应答直接返回，特别适合于批量处理
     * @param key 键
     */
    public void deleteWithNoReply(String key) throws InterruptedException, MemcachedException, TimeoutException {
        memcachedClient.deleteWithNoReply(key);
    }

    /**
     * 清空全部缓存 cache ,谨慎使用 真正项目上禁用
     */
    public void flushAll() throws InterruptedException, MemcachedException, TimeoutException {
        memcachedClient.flushAll();
    }


    /*=================【6、定义数值操作incr/decr方法】===================*/

    /**
     * 递增++ ：每次递增 1 ,永久存储
     *
     * @param key 键 若不存，默认初始值为0，递增因子1
     */
    public long incr(String key) throws InterruptedException, MemcachedException, TimeoutException {
        return this.incr(key, 1);
    }

    /**
     * 递增++ : 自定义递增因子大小,永久存储
     *
     * @param key   键 若不存，默认初始值为0，递增因子1
     * @param delta 递增因子必须大于0
     */
    public long incr(String key, long delta) throws InterruptedException, MemcachedException, TimeoutException {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return memcachedClient.incr(key, delta);
    }

    /**
     * 递增++ : 永久存储, 第一个参数指定递增的key名称，第二个参数指定递增的幅度大小，第三个参数指定当key不存在的情况下的初始值
     *
     * @param key   指定递增的key名称
     * @param delta 指定递增的幅度大小
     * @param initValue  指定当key不存在的情况下的初始值
     */
    public long incr(String key, long delta, long initValue) throws InterruptedException, MemcachedException, TimeoutException {
        if (delta < 0 || initValue < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return memcachedClient.incr(key, delta, initValue);
    }

    /**
     * 递减-- ：永久存储，每次递减 1
     * @param key 键
     */
    public long decr(String key) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.decr(key, 1);
    }

    /**
     * 递减-- ：永久存储，指定递减值
     * @param key 键
     * @param delta 递减因子
     */
    public long decr(String key, long delta) throws InterruptedException, MemcachedException, TimeoutException {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return memcachedClient.decr(key, delta);
    }

    /**
     * 递减-- ：永久存储，指定key、递减因子、初始值
     * @param key 键
     * @param delta 递减因子
     * @param init 初始值，若key值不存在，以初始值递减
     */
    public long decr(String key, long delta, long init) throws InterruptedException, MemcachedException, TimeoutException {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return memcachedClient.decr(key, delta, init);
    }

    /*=================【7、定义append/prepend方法 】===================*/

    /**
     * append在原有的key的value的末尾追加值，如果key不存在，则追加失败
     * @param key 键
     * @param value 末尾追加的值
     */
    public boolean append(String key,Object value) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.append(key,value);
    }

    /**
     * append在原有的key的value的末尾追加值，如果key不存在，则追加失败
     * @param key 键
     * @param value 末尾追加的值
     * @param l
     */
    public boolean append(String key,Object value,Long l) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.append(key,value,l);
    }


    /**
     * prepend在原有的value的头位置添加值,如果key不存在，则添加失败
     * @param key
     * @param value
     */
    public boolean prepend(String key,Object value) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.prepend(key,value);
    }

    /**
     * prepend在原有的value的头位置添加值,如果key不存在，则添加失败
     * @param key 键
     * @param value 在头位置添加的值
     * @param l 第几个位置添加
     */
    public boolean prepend(String key,Object value,Long l) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.prepend(key,value,l);
    }

    /**
     * prepend在原有的value的头位置添加值，不返回添加结果,如果key不存在，则添加失败
     * @param key 键
     * @param value 在头位置添加的值
     */
    public void prependWithNoReply(String key,Object value) throws MemcachedException, InterruptedException {
        memcachedClient.prependWithNoReply(key,value);
    }



    /*=================【8、定义cas方法-解决并发情况 】===================*/

    /**
     * 每次操作，cas的id都为递增,并且cas的key一定要存在，要不然会执行失败
     * @param key 键
     * @param expiry 时间
     * @param newValue 新值
     * @param cas  cas版本号,通过gets 结果集中获取cas
     */
    public boolean cas(String key, int expiry, Object newValue,long cas) throws InterruptedException, MemcachedException, TimeoutException {
        return memcachedClient.cas(key,expiry,newValue,cas);
    }

}
