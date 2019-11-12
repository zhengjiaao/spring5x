package com.zja.controller;

import com.zja.entity.UserDTO;
import com.zja.util.XMemcachedUtil;
import net.rubyeye.xmemcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhengJa
 * @description Memcached 缓存测试
 * @data 2019/11/12
 */

@RestController
@RequestMapping("rest/xmemcached")
public class XMemcachedController {

    @Autowired
    private MemcachedClient memcachedClient;

    @Autowired
    private XMemcachedUtil xMemcachedUtil;

    //操作字符串

    /**
     * 字符串保存
     * @param
     * @return java.lang.Object
     */
    @GetMapping("save/str")
    public Object saveStr() throws Exception {

        //保存到Memcached缓存中
        //boolean result = memcachedClient.set("xmemcached", 0, "使用 xmemcached 存储数据");
        boolean result = xMemcachedUtil.addCache("xmemcached","使用 xmemcached 存储数据",0);

        System.out.println("保存结果 result= "+result);

        //从缓存中获取数据
        String value = memcachedClient.get("xmemcached");
        System.out.println("获取value=" + value);
        return value;
    }

    @GetMapping("delete/str")
    public Object deleteStr() throws Exception {
        //删除缓存中的数据
        boolean delete = memcachedClient.delete("xmemcached");
        System.out.println("删除结果 delete: "+delete);

        //再次获取数据
        String value2 = memcachedClient.get("xmemcached");
        System.out.println("获取 value2=" + value2);
        return "执行成功!";
    }

    //操作对象

    /**
     * 对象保存-对象需要序列化
     * @param
     * @return java.lang.Object
     */
    @GetMapping("save/object")
    public Object saveObject() throws Exception {

        UserDTO userDTO =new UserDTO();
        userDTO.setId("1");
        userDTO.setAge(21);
        userDTO.setUsername("李四");

        //保存到Memcached缓存中
        boolean result = memcachedClient.set("userdto", 0, userDTO);
        System.out.println("保存结果 result= "+result);

        //从缓存中获取数据
        UserDTO userdto = memcachedClient.get("userdto");
        System.out.println("获取userdto=" + userdto);
        return userdto;
    }

    @GetMapping("delete/object")
    public Object deleteObject() throws Exception {
        //删除缓存中的数据
        boolean delete = memcachedClient.delete("userdto");
        System.out.println("删除结果 delete: "+delete);

        //再次从缓存中获取数据
        UserDTO userdto = memcachedClient.get("userdto");
        System.out.println("获取userdto=" + userdto);
        return userdto;
    }



}
