![Spring5x-swagger2-目录.png](https://upload-images.jianshu.io/upload_images/15645795-1faff160cc6fcc8d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


Spring5x-swagger2此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请先参考：  [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

* 1、spring mvc的 注解
* 2、maven profile搭建多环境
* 3、集成swagger2 API
    *  swagger2 接口、参数及实体类的注解使用
    *  swagger2 在多环境中的启动和关闭
    



#### 1、spring mvc 注解

```java
package com.zja.controller;

import com.zja.entity.DemoEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description spring MVC 注解
 * @data 2019/10/24
 */
@RestController
@RequestMapping("rest/annotations")
public class AnnotationsController {

    /* *****************无参请求******************** */

    //get 请求无参注解
    @GetMapping("get/test")
    public String getTest() {
        return "这是getTest";
    }

    //post请求
    @PostMapping("post/test")
    public String postTest() {
        return "这是postTest";
    }

    //delete请求
    @DeleteMapping("delete/test")
    public String deleteTest() {
        return "这是deleteTest";
    }

    //put请求
    @PutMapping("put/test")
    public String putTest() {
        return "这是putTest";
    }

    /* *****************有参请求 str 类型 ******************** */

    //get 请求带参
    @GetMapping("get/test2")
    public String getTest2(@RequestParam String name,
                           @RequestParam String value) {
        System.out.println("name= " + name);
        return "这是getTest2";
    }

    //post 请求带参
    @PostMapping("post/test2")
    public String postTest2(@RequestBody String name) {
        System.out.println("name= " + name);
        return "这是postTest2";
    }

    /* *****************有参请求 entity 类型 ******************** */

    //post请求带参 和 PostMapping 一样
    @RequestMapping(value = "post/test3", method = RequestMethod.POST)
    public String postTest3(@RequestBody DemoEntity demoEntity) {
        System.out.println("post-demoEntity= " + demoEntity);
        return "这是postTest3";
    }
}


```

> 仅演示 controller 层接口注解



#### 2、集成 maven profile搭建多环境

说明：**spring + maven 的项目**

**结构图**：
![maven profile.png](https://upload-images.jianshu.io/upload_images/15645795-b77774eb4e73d375.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

前提：spring-mvc.xml 引入.properties文件

```xml
<!--资源文件导入 只能导入properties-->
<context:property-placeholder location="classpath:config/*.properties"/>

```



​	2.1 pom.xml 配置 profile

```xml
<profiles>
        <!--本地开发环境-->
        <profile>
            <id>local</id>
            <properties>
                <!--注：与结构木文件夹名称一致-->
                <profiles.active>local</profiles.active>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <!--测试环境-->
        <profile>
            <id>test</id>
            <properties>
                <profiles.active>test</profiles.active>
            </properties>
        </profile>
        <!--生产环境-->
        <profile>
            <id>product</id>
            <properties>
                <profiles.active>product</profiles.active>
            </properties>
        </profile>
    </profiles>

```

​	2.2 pom.xml 配置 resource

```xml
	<build>
        <finalName>spring5x-web</finalName>
        <resources>
            <resource>
                <!--${profiles.active} 与上方配置的<profiles.active> 一致 -->
                <directory>src/main/profiles/${profiles.active}</directory>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
    </build>

```

​	2.3 IDEA 测试

idea：注意，要先执行清除-->编译或打包命令-->执行启动项目测试

不然会发现，项目启动时所有的配置都会被打包进来。
![IDEA profiles.png](https://upload-images.jianshu.io/upload_images/15645795-321d099806fb6eda.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

​	2.4 maven 命令测试

> 命令执行打包: mvn clean package -Dmaven.test.skip=true -P标识

```python
例(local环境打包测试): mvn clean package -Dmaven.test.skip=true -Plocal

```

​	2.5 效果：
![maven profile效果.png](https://upload-images.jianshu.io/upload_images/15645795-08d66bd6ea89d88d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 3、集成swagger2 API

​	3.1 swagger2 api集成

​		pom.xml 引入依赖

```xml
	<properties>
		<!--spring5.x集成swagger2-->
        <springfox.version>2.9.2</springfox.version>
    </properties>

	   <!--spring5.x 集成swagger2-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>${springfox.version}</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>${springfox.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.6</version>
        </dependency>
```

​		Swagger2Config.java  swagger配置类

```java
package com.zja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ZhengJa
 * @description spring5.x集成swagger2 API 测试功能
 * @data 2019/10/22
 */
@Component
@Configuration
@EnableWebMvc
@EnableSwagger2
@ComponentScan(basePackages = {"com.zja.controller"})
public class Swagger2Config extends WebMvcConfigurationSupport {

    //是否开启swagger，一般生产环境关闭swagger，在测试和开发环境开启
    @Value("${swaggerShow}")
    private boolean swaggerShow;

    @Bean
    public Docket createRestApi() {
        return new Docket(
                DocumentationType.SWAGGER_2)
                .enable(swaggerShow)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Zhengja", "https://www.jianshu.com/u/70d69269bd09", "1263598336@qq.com");
        return new ApiInfoBuilder()
                .title("Swagger2 API 测试")
                .description("宇宙小神特别萌")
                .contact(contact)
                .version("V_1.0.1")
                .build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (this.swaggerShow) {
            registry.addResourceHandler("/swagger-ui.html").addResourceLocations(
                    "classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
    }

}

```



​	3.2 swagger2 在多环境中的启动和关闭

​		application-dev.properties 本地开发环境配置

```properties
# 是否启用swagger：true/false 比如开发测试启动，生成线上关闭swagger功能
swaggerShow=true

```

​		application-prod.properties 生产环境配置

```properties
# 是否启用swagger：true/false 比如开发测试启动，生成线上关闭swagger功能
swaggerShow=false

```

​		application-test.properties 测试环境配置

```properties
# 是否启用swagger：true/false 比如开发测试启动，生成线上关闭swagger功能
swaggerShow=true

```



​	3.3 swagger2 接口、参数及实体类的注解使用
​		SwaggerController.java 接口和参数 swagger注解的使用

```java
package com.zja.controller;

import com.zja.entity.DemoEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * @author ZhengJa
 * @description spring 集成swagger测试
 * @data 2019/10/24
 */
@Api(value = "web-SwaggerController")
@RestController
@RequestMapping("rest/swagger")
public class SwaggerController {

    /* *****************无参请求******************** */

    @ApiOperation(value = "接口功能说明：查询接口", notes = "注释：get无参请求", httpMethod = "GET")
    @GetMapping("get/test")
    public String getTest() {
        return "这是getTest";
    }

    @ApiOperation(value = "保存接口", httpMethod = "POST")
    @PostMapping("post/test")
    public String postTest() {
        return "这是postTest";
    }

    @ApiOperation(value = "删除接口", notes = "这是删除请求", httpMethod = "DELETE")
    @DeleteMapping("delete/test")
    public String deleteTest() {
        return "这是deleteTest";
    }

    @ApiOperation(value = "更新接口", notes = "这是数据更新接口", httpMethod = "PUT")
    @PutMapping("put/test")
    public String putTest() {
        return "这是putTest";
    }

    /* *****************有参请求 str 类型 ******************** */

    @ApiOperation(value = "str-查询接口", notes = "str-get传参方式", httpMethod = "GET")
    @GetMapping("get/test2")
    public String getTest2(@ApiParam(value = "name参数说明", defaultValue = "get-name的默认值") @RequestParam String name,
                           @ApiParam(value = "value：必须传参", required = true) @RequestParam String value) {
        System.out.println("name= " + name);
        return "这是getTest2";
    }

    @ApiOperation(value = "str-保存接口", notes = "str-post传参方式", httpMethod = "POST")
    @PostMapping("post/test2")
    public String postTest2(@ApiParam(value = "name参数说明", defaultValue = "post-name的默认值") @RequestBody String name) {
        System.out.println("name= " + name);
        return "这是postTest2";
    }

    /* *****************有参请求 entity 类型 ******************** */

    @ApiOperation(value = "entity-保存接口", notes = "enrity-post传参方式", httpMethod = "POST")
    @RequestMapping(value = "post/test3", method = RequestMethod.POST)
    public String postTest3(@ApiParam(value = "name参数说明", defaultValue = "post-name的默认值") @RequestBody DemoEntity demoEntity) {
        System.out.println("post-demoEntity= " + demoEntity);
        return "这是postTest3";
    }


}



```

​		DemoEntity.java 实体类swagger使用

```java
package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author ZhengJa
 * @description Dome
 * @data 2019/10/24
 */
@ApiModel("Demo实体类")
public class DemoEntity implements Serializable {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "密码")
    private String pwd;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}


```



**搭建已经可以结束!!!**



## github 地址：
* [https://github.com/zhengjiaao/spring5x](https://github.com/zhengjiaao/spring5x)


## 博客地址
* 简书：https://www.jianshu.com/u/70d69269bd09
* 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts

