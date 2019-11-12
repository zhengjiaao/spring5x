![spring5.x-mongodb目录.png](https://upload-images.jianshu.io/upload_images/15645795-4fe5474756e82313.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

spring5x-mongodb 此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构
如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- 1、spring5 mongodb 依赖及配置
- 2、MongoTemplate 对文档操作
- 3、GridFsTemplate 对文件操作
- 4、演示(文件上传和在线查看)



### 1、spring5 mongodb 依赖及配置

****

pom.xml 依赖

```xml
		<!--MongoDB核心包-->
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver</artifactId>
            <version>3.6.4</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-mongodb</artifactId>
            <version>2.0.10.RELEASE</version>
            <scope>compile</scope>
            <exclusions>
                <exclusion>
                    <artifactId>mongo-java-driver</artifactId>
                    <groupId>org.mongodb</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>jcl-over-slf4j</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>slf4j-api</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>

		<!-- 其它的依赖(不包含spring) -->

        <!--IOUtils工具使用-->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.1</version>
        </dependency>
        <!--spring mvc的文件上传类 CommonsMultipartResolver 依赖了这个包-->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.3</version>
        </dependency>
        <!-- 使用lombok实现JavaBean的get、set、toString、hashCode、equals等方法的自动生成  -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
            <optional>true</optional>
        </dependency>

```

mongodb.properties

```properties
# ip
mongo.host=127.0.0.1
# 端口
mongo.port=27017
# 数据库名称. 默认是'db'
mongo.dbname=test
# 用户名
mongo.username=test
# 密码
mongo.password=123456
# 凭证：认证方式  用户:密码@数据库名称
mongo.credentials=test:123456@test

# 每个主机允许的连接数
mongo.connectionsPerHost=10
# 线程队列数，它和上面connectionsPerHost值相乘的结果就是线程队列最大值。如果连接线程排满了队列就会抛出异常
mongo.threadsAllowedToBlockForConnectionMultiplier=5
# 连接超时的毫秒 0是默认值且无限大。
mongo.connectTimeout=1000
# 最大等待连接的线程阻塞时间 默认是120000 ms (2 minutes).
mongo.maxWaitTime=1500
# 保持活动标志，控制是否有套接字保持活动超时 官方默认为true 且不建议禁用
mongo.socketKeepAlive=true
# 用于群集心跳的连接的套接字超时。
mongo.socketTimeout=1500

```

> 注意：这里虽然有用户密码，但是我没有配置，应为我的mongodb没有设置用户密码认证

spring-mongodb.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:mongo="http://www.springframework.org/schema/data/mongo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd
       http://www.springframework.org/schema/data/mongo
       http://www.springframework.org/schema/data/mongo/spring-mongo.xsd">

    <!-- 加载mongodb的属性配置文件 -->
    <context:property-placeholder location="classpath:mongodb/mongodb.properties" ignore-unresolvable="true"/>

    <!--定义用于访问 MongoDB 的 MongoClient 实例 ,凭证：认证 credentials="${mongo.credentials}" -->
    <mongo:mongo-client id="mongoClient" host="${mongo.host}" port="${mongo.port}">
        <mongo:client-options
                connections-per-host="${mongo.connectionsPerHost}"
                threads-allowed-to-block-for-connection-multiplier="${mongo.threadsAllowedToBlockForConnectionMultiplier}"
                connect-timeout="${mongo.connectTimeout}"
                max-wait-time="${mongo.maxWaitTime}"
                socket-keep-alive="${mongo.socketKeepAlive}"
                socket-timeout="${mongo.socketTimeout}"
        />
    </mongo:mongo-client>

    <!--- 方式一： -->
    <!--<mongo:repositories base-package="com.zja.dao.mongo"/>-->

    <!-- 方式二： -->

    <!--定义用于连接到数据库的连接工厂-->
    <mongo:db-factory dbname="${mongo.dbname}" id="mongoDbFactory" mongo-ref="mongoClient"/>

    <!--转换器-->
    <mongo:mapping-converter id="converter" db-factory-ref="mongoDbFactory"/>

    <!-- mongodb的主要操作对象，所有对mongodb的增删改查的操作都是通过它完成 -->
    <bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
        <constructor-arg name="mongoDbFactory" ref="mongoDbFactory"/>
    </bean>

    <!--mongo存储文件：主要操作文件存储和下载-->
    <bean id="gridFsTemplate" class="org.springframework.data.mongodb.gridfs.GridFsTemplate">
        <constructor-arg ref="mongoDbFactory"/>
        <constructor-arg ref="converter"/>
    </bean>

</beans>

```

spring-mvc.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.zja.*"/>

    <!--使用默认的 Servlet 来响应静态文件 -->
    <mvc:default-servlet-handler/>

    <!-- 开启springMVC 注解驱动 -->
    <mvc:annotation-driven>
        <mvc:message-converters register-defaults="false">
            <!-- 将StringHttpMessageConverter的默认编码设为UTF-8 ，解决返回给前端中文乱码-->
            <bean class="org.springframework.http.converter.StringHttpMessageConverter">
                <constructor-arg value="UTF-8"/>
            </bean>
            <!-- 将Jackson2HttpMessageConverter的默认格式化输出设为true -->
            <bean class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
                <property name="prettyPrint" value="true"/>
                <property name="supportedMediaTypes">
                    <list>
                        <!-- 优先使用该媒体类型,为了解决IE浏览器下,返回JSON数据的下载问题 -->
                        <value>application/json;charset=UTF-8</value>
                        <value>text/html;charset=UTF-8</value>
                        <value>text/json;charset=UTF-8</value>
                    </list>
                </property>
                <!-- 使用内置日期工具进行处理 -->
                <property name="objectMapper">
                    <bean class="com.fasterxml.jackson.databind.ObjectMapper">
                        <property name="dateFormat">
                            <bean class="java.text.SimpleDateFormat">
                                <constructor-arg type="java.lang.String" value="yyyy-MM-dd"/>
                            </bean>
                        </property>
                    </bean>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>

    <!-- 增加application.properties文件 -->
    <context:property-placeholder
            location="classpath:application.properties" ignore-unresolvable="true"/>

    <!--spring-mongodb.xml 配置-->
    <import resource="classpath:mongodb/spring-mongodb.xml"/>

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 ：/WEB-INF/jsp/和/WEB-INF/html/-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 ：.jsp和.html-->
        <property name="suffix" value=".jsp"/>
    </bean>

    <!-- =================== springmvc文件上传和下载 ================== -->
    <!--配置文件上传 id值固定 -->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <!-- 限制文件上传总大小，不设置默认没有限制，单位为字节 200*1024*1024即200M -->
        <property name="maxUploadSize" value="209715200" />
        <!-- 设置每个上传文件的大小上限 -->
        <property name="maxUploadSizePerFile" value="1048576"/>
        <!-- 处理文件名中文乱码 -->
        <property name="defaultEncoding" value="UTF-8" />
        <!-- resolveLazily属性启用是为了推迟文件解析，以便捕获文件大小异常 -->
        <property name="resolveLazily" value="true" />
    </bean>

</beans>

```

> 注：
> 1、spring-mongodb.xml 配置
> 2、multipartResolver 配置 是为了控制文件上传大小



### 2、MongoTemplate 对文档操作

****

MongoTemplateController.java

```java
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

```

> 对文档的增删改查



### 3、GridFsTemplate 对文件操作

****

MongodbConfig.java

```java
package com.zja.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

import javax.annotation.Resource;

/** Mongodb 配置类
 * @author zhengja@dist.com.cn
 * @data 2019/6/19 17:26
 */
@Configuration
public class MongodbConfig {

    @Resource
    private MongoDbFactory mongoDbFactory;

    @Bean
    public GridFSBucket getGridFSBuckets() {
        MongoDatabase db = mongoDbFactory.getDb();
        return GridFSBuckets.create(db);
    }
}

```

> 操作文件：下载和在线查看文件，需要用的打开流



GridFsTemplateController.java

```java
package com.zja.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhengJa
 * @description 文件存储 操作
 * @data 2019/11/12
 */
@RestController
@RequestMapping("rest/gridfs")
public class GridFsTemplateController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Resource
    private GridFSBucket gridFSBucket;

    /**  文件上传测试：推荐用Postman工具请求测试 参考连接：https://www.jianshu.com/p/ba899556e02a **/

    /**文件上传测试：推荐用Postman工具测试
     * 单文件上传到-mongodb
     * @param file
     * @return java.lang.String
     */
    @RequestMapping(value = "v1/save/file",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public String saveGridfs(@RequestParam(value = "file", required = true) MultipartFile file){

        System.out.println("Saving file..");
        DBObject metaData = new BasicDBObject();
        metaData.put("createdDate", new Date());

        String fileName = UUID.randomUUID().toString();

        System.out.println("File Name: " + fileName);

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            gridFsTemplate.store(inputStream, fileName, "image", metaData);
            System.out.println("File saved: " + fileName);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            throw new RuntimeException("System Exception while handling request");
        }
        System.out.println("File return: " + fileName);
        return fileName;
    }

    /** 文件上传到mongodb：推荐用Postman工具测试
     * 多/单文件批量上传-mongodb :文件默认是上传到数据中的fs.files和fs.chunks中,files是用来存储文件的信息，文件名，md5,文件大小，还有刚刚的metadata,上传时间等等数据
     * @param multipartFiles
     * @return java.lang.Object
     */
    @RequestMapping(value = "v1/save/Manyfile", method = RequestMethod.POST)
    public Object uploadManyFile1(@RequestParam(value = "file") MultipartFile[] multipartFiles) throws Exception {
        Map map = new HashMap();
        System.out.println("multipartFiles.length " + multipartFiles.length);
        if (multipartFiles != null && multipartFiles.length > 0) {
            int count = 0;
            for (MultipartFile multipartFile : multipartFiles) {
                // 获得提交的文件名
                String fileName = multipartFile.getOriginalFilename();
                // 获得文件输入流
                InputStream ins = multipartFile.getInputStream();
                // 获得文件类型
                String contentType = multipartFile.getContentType();
                //存储文件的额外信息，比如用户ID,后面要查询某个用户的所有文件时就可以直接查询,可以不传
                DBObject metadata = new BasicDBObject("userId", "1001");

                // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
                ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType);

                // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
                // ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType, metadata);

                String fileInfo = "文件名称:\n"+fileName+"\n"+"fileId:"+"\n"+objectId.toString()+"\n"+"文件具体信息:\n"+objectId;

                map.put("fileInfo" + count,fileInfo);

                count++;
            }
        }
        return map;
    }

    /**
     * 在线查看Mongo中的文件：根据名称获取文件并在线查看
     * @param fileName
     * @param response
     * @return int
     */
    @RequestMapping(value = "v1/get/file", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public int getGridfs(@RequestParam(value = "fileName", required = true) String fileName,
                         HttpServletResponse response) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(fileName)));
        if (gridFSFile==null){
            System.out.println("File not found" + fileName);
            throw new RuntimeException("No file with name: " + fileName);
        }
        GridFsResource gridFsResource = convertGridFSFileToResource(gridFSFile);
        return IOUtils.copy(gridFsResource.getInputStream(),response.getOutputStream());
    }

    /**
     * 下载Mongo文件：根据名称下载文件
     * @param fileName
     * @param response
     * @return int
     */
    @RequestMapping(value = "v1/download/file", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public int downloadGridfs(@RequestParam(value = "fileName", required = true) String fileName,
                              HttpServletResponse response) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(fileName)));
        if (gridFSFile==null){
            System.out.println("File not found" + fileName);
            throw new RuntimeException("No file with name: " + fileName);
        }
        GridFsResource gridFsResource = convertGridFSFileToResource(gridFSFile);
        OutputStream sos = response.getOutputStream();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        return IOUtils.copy(gridFsResource.getInputStream(),response.getOutputStream());
    }
    
   /**
     * 删除Mongo中的文件：根据名称删除文件
     * @param fileName 文件名
     * @return void
     */
    @RequestMapping(value = "v1/delete/file", method = RequestMethod.DELETE)
    public void deleteGridfs(@RequestParam(value = "fileName", required = true) String fileName) {
        System.out.println("Deleting file.." + fileName);
        gridFsTemplate.delete(new Query().addCriteria(Criteria.where("filename").is(fileName)));
        System.out.println("File deleted " + fileName);
    }

    /**
     * 转换器：将GridFSFile转为GridFsResource
     * @param gridFsFile
     * @return org.springframework.data.mongodb.gridfs.GridFsResource
     */
    private GridFsResource convertGridFSFileToResource(GridFSFile gridFsFile) {
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
        return new GridFsResource(gridFsFile, gridFSDownloadStream);
    }

}

```

> 对文件的操作：
> 1、上传单个文件到mongodb
> 2、上传多个或单个文件到mongodb
> 3、在线查看Mongo中的文件
> 4、下载Mongo文件
> 5、删除Mongo中的文件



### 4、演示(文件上传和在线查看)

****

文件上传：![mongo_1.png](https://upload-images.jianshu.io/upload_images/15645795-ad1455ef1bddd12a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
****
文件在线查看：![mongo_2.png](https://upload-images.jianshu.io/upload_images/15645795-72195635b899736c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)



## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts

