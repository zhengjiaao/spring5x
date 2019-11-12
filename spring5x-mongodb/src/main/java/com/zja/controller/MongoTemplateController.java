package com.zja.controller;

import com.mongodb.client.result.DeleteResult;
import com.zja.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZhengJa
 * @description Mongo 文档数据操作
 * @data 2019/11/8
 */
@RestController
@RequestMapping("rest/mongo")
public class MongoTemplateController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("save/data")
    public Object insert() {

        UserDTO userDTO = new UserDTO();
        userDTO.setId("1");
        userDTO.setUsername("1");
        userDTO.setAge(1);

        //userDTO 要保存文档的数据 ，users集合    由于UserDTO类上有注解：@Document(collection = "users")
        mongoTemplate.save(userDTO);

        userDTO.setId("2");
        userDTO.setUsername("2");
        userDTO.setAge(2);
        //userDTO 要保存文档的数据 ，users集合    由于UserDTO类上有注解：@Document(collection = "users")
        mongoTemplate.save(userDTO);

        //根据条件查询 注意是 _id  ，也可以用name等其它字段做条件
        Query query = new Query(Criteria.where("_id").is("2"));

        List<UserDTO> userDTOS = mongoTemplate.find(query, UserDTO.class);

        return userDTOS;
    }

    /**
     * 根据id查询users文档数据
     *
     * @param id
     * @return java.lang.Object
     */
    @RequestMapping(value = "v2/users/getUser", method = RequestMethod.GET)
    public Object getUser(@RequestParam String id) {
        //根据条件查询 注意是 _id  ，也可以用name等其它字段做条件
        Query query = new Query(Criteria.where("_id").is(id));

        List<UserDTO> userDTOS = mongoTemplate.find(query, UserDTO.class);

        return userDTOS;
    }

    /**
     * 查询所有users文档数据
     *
     * @param
     * @return java.lang.Object
     */
    @RequestMapping(value = "v2/users/getAllUser", method = RequestMethod.GET)
    public Object getAllUser() {

        List<UserDTO> userDTOS = mongoTemplate.findAll(UserDTO.class);

        return userDTOS;
    }

    /**
     * 删除单条users文档数据
     *
     * @param id
     * @return java.lang.Object
     */
    @RequestMapping(value = "v2/users/deleteUser", method = RequestMethod.DELETE)
    public Object deleteUser(@RequestParam String id) {
        //根据条件查询 注意是 _id  ，也可以用name等其它字段做条件
        Query query = new Query(Criteria.where("_id").is(id));

        DeleteResult deleteResult = mongoTemplate.remove(query, UserDTO.class);

        Map map = new HashMap();
        map.put("wasAcknowledged", deleteResult.wasAcknowledged());
        map.put("getDeletedCount", deleteResult.getDeletedCount());

        return map;
    }

    /**
     * 删除所有users文档数据
     *
     * @param
     * @return java.lang.Object
     */
    @RequestMapping(value = "v2/users/deleteAllUser", method = RequestMethod.DELETE)
    public Object deleteAllUser() {

        Map map = new HashMap();
        List<UserDTO> userDTOS = (List<UserDTO>) getAllUser();
        int count = 0;
        for (UserDTO userDTO : userDTOS) {
            Object o = deleteUser(userDTO.getId());
            map.put("删除的第几条数据-" + count, o);
            count++;
        }
        return map;
    }

}
