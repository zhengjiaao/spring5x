![spring5.x-springmvc-目录.png](https://upload-images.jianshu.io/upload_images/15645795-4b8a6d95fe193041.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


spring5.x-springmvc此模块是从spring5x-base 基础模块扩展过来的
spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请先参考：  [spring5x-base模块搭建](https://www.jianshu.com/p/8612404cf1d6)


## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- swagger2 API
- 参数校验
- 文件上传和下载
- 参数绑定
- Restful 风格的请求
- 全局异常处理：自定义异常
- 拦截器 Interceptor
- 全局时间格式配置
- log4j2 日志配置
- lombok 简化实体类代码
- spring AOP 配置



### 1、新增swagger2 API

****
pom.xml 配置依赖

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

config 配置swagger2

```java
package com.zja.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = {"com.zja.controller"})
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().apiInfo(apiInfo());
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
}

```

spring-mvc.xml 最下面配置swagger2

```xml
<!-- =================== swagger2 config ================== -->
    <bean class="com.zja.config.Swagger2Config"/>
    <mvc:resources mapping="swagger-ui.html" location="classpath:/META-INF/resources/"/>
    <mvc:resources mapping="/webjars/**" location="classpath:/META-INF/resources/webjars/"/>

```

详细使用参考地址：百度 **swagger2 使用**



### 2、参数校验

****

pom.xml 配置依赖包

```xml
	<!-- 数据校验依赖包 -->
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>6.0.13.Final</version>
        </dependency>
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>

```

配置实体类属性

```java
package com.zja.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @author ZhengJa
 * @description 测试参数绑定的类
 * @data 2019/10/22
 */
@ApiModel(value = "Programmer")
@Data
public class Programmer implements Serializable {

    private String name;

    //数据校验
    @Min(value = 0,message = "年龄不能为负数！" )
    private int age;

    @Min(value = 0,message = "薪酬不能为负数！" )
    private double salary;

    @ApiModelProperty(value = "默认: 2019-10-22 09:02:02")
    private String birthday;
}


```

> 参数校验：前端传值 age和salary的值，@Min 最小不能小于value = 0 的值

ParamValidController.java

```java
package com.zja.controller;

import com.zja.entity.Programmer;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ZhengJa
 * @description 校验参数数据
 * @data 2019/10/22
 */
@RestController
public class ParamValidController {

    @PostMapping("validate")
    public void valid(@RequestBody @Validated Programmer programmer, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            System.out.println(error.getDefaultMessage());
        }
    }
}

```

详细使用参考地址：https://www.jianshu.com/p/0bfe2318814f



### 3、文件上传和下载

****

pom.xml 引入依赖

```xml
	    <!--spring 的文件上传类 CommonsMultipartResolver 依赖了这个包-->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.3</version>
        </dependency>

```

spring-mvc.xml 配置上传和下载的限制

```xml
<!-- =================== springmvc文件上传和下载 ================== -->
    <!--配置文件上传-->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="maxUploadSize" value="102400000"/>
        <property name="maxUploadSizePerFile" value="10240000"/>
        <property name="defaultEncoding" value="utf-8"/>
    </bean>

```

FileController.java

```java
package com.zja.controller;

import com.zja.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author ZhengJa
 * @description 文件上传测试
 * @data 2019/10/22
 */
@Controller
public class FileController {


    @GetMapping("file")
    public String filePage() {
        return "file";
    }


    /***
     * 单文件上传
     */
    @PostMapping("upFile")
    public String upFile(MultipartFile file, HttpSession session) {
        FileUtil.saveFile(file, session.getServletContext().getRealPath("/image"));
        return "success";
    }

    /***
     * 多文件上传 多个文件用同一个名字
     */
    @PostMapping("upFiles")
    public String upFiles(@RequestParam(name = "file") MultipartFile[] files, HttpSession session) {
        for (MultipartFile file : files) {
            FileUtil.saveFile(file, session.getServletContext().getRealPath("images"));
        }
        return "success";
    }

    /***
     * 多文件上传方式2 分别为不同文件指定不同名字
     */
    @PostMapping("upFiles2")
    public String upFile(String extendParam,
                         @RequestParam(name = "file1") MultipartFile file1,
                         @RequestParam(name = "file2") MultipartFile file2, HttpSession session) {
        String realPath = session.getServletContext().getRealPath("images2");
        FileUtil.saveFile(file1, realPath);
        FileUtil.saveFile(file2, realPath);
        System.out.println("extendParam:" + extendParam);
        return "success";
    }


    /***
     * 上传用于下载的文件
     */
    @PostMapping("upFileForDownload")
    public String upFileForDownload(MultipartFile file, HttpSession session, Model model) throws UnsupportedEncodingException {
        String path = FileUtil.saveFile(file, session.getServletContext().getRealPath("/image"));
        model.addAttribute("filePath", URLEncoder.encode(path,"utf-8"));
        model.addAttribute("fileName", file.getOriginalFilename());
        return "fileDownload";
    }

    /***
     * 下载文件
     */
    @GetMapping("download")
    public ResponseEntity<byte[]> downloadFile(String filePath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File file = new File(filePath);
        // 解决文件名中文乱码
        String fileName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

}

```



**jsp页面放在webapp/WEB-INF/jsp下**
file.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/file.css">
</head>
<body>

    <form action="${pageContext.request.contextPath}/upFile" method="post" enctype="multipart/form-data">
        请选择上传文件：<input name="file" type="file"><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath}/upFiles" method="post" enctype="multipart/form-data">
        请选择上传文件(多选)：<input name="file" type="file" multiple><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath}/upFiles2" method="post" enctype="multipart/form-data">
        请选择上传文件1：<input name="file1" type="file"><br>
        请选择上传文件2：<input name="file2" type="file"><br>
        文件内容额外备注: <input name="extendParam" type="text"><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath}/upFileForDownload" method="post" enctype="multipart/form-data">
        上传并下载：<input name="file" type="file"><br>
        <input type="submit" value="点击上传文件">
    </form>

</body>
</html>

```

fileDownload.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件下载</title>
</head>
<body>
    <a href="${pageContext.request.contextPath}/download?filePath=${filePath}">${fileName}</a>
</body>
</html>

```

success.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    操作成功！
</body>
</html>

```



### 4、参数绑定

****

param.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Restful</title>
    <!--jquery3.3.1.js 需要自己网上下载-->
    <script src="${pageContext.request.contextPath}/js/jquery3.3.1.js"></script>
</head>
<body>
<ul>
    <li>姓名：${empty name ? p.name : name}</li>
    <li>年龄：${empty age ? p.age : age}</li>
    <li>薪酬：${empty salary ? p.salary : salary}</li>
    <li>生日：${empty birthday ? p.birthday : birthday}</li>
</ul>
</body>
</html>

```

ParamBindController.java

```java
package com.zja.controller;

import com.zja.entity.Programmer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author ZhengJa
 * @description 参数绑定测试
 * @data 2019/10/22
 */
@Api(value = "ParamBindController")
@Controller
public class ParamBindController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    }

    @ApiOperation(value = "param-参数绑定与日期格式转换", httpMethod = "GET")
    @RequestMapping(value = "param", method = RequestMethod.GET)
    public String param(@ApiParam("name") @RequestParam String name,
                        @ApiParam("age") @RequestParam int age,
                        @ApiParam("salary") @RequestParam double salary,
                        @ApiParam(value = "birthday",defaultValue = "2019-10-22 09:02:02") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date birthday,
                        Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("salary", salary);
        model.addAttribute("birthday", birthday);
        return "param";
    }

    @ApiOperation(value = "param2-参数绑定与日期格式转换", httpMethod = "GET")
    @RequestMapping(value = "param2", method = RequestMethod.GET)
    public String param2(@ApiParam("name") @RequestParam String name,
                         @ApiParam("age") @RequestParam int age,
                         @ApiParam("salary") @RequestParam double salary,
                         @ApiParam(value = "birthday",defaultValue = "2019-10-22 09:02:02") @RequestParam Date birthday,
                         Model model) {
        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("salary", salary);
        model.addAttribute("birthday", birthday);
        return "param";
    }

    @PostMapping("param3")
    public String param3(Programmer programmer,
                         String extendParam, Model model) {
        System.out.println("extendParam" + extendParam);
        model.addAttribute("p", programmer);
        return "param";
    }

}

```



### 5、全局异常处理：自定义异常

****

NoAuthException.java 自定义异常

```java
package com.zja.AuthException;

/**
 * @author ZhengJa
 * @description 自定义异常:全局异常处理
 * @data 2019/10/22
 */
public class NoAuthException extends RuntimeException{
    public NoAuthException() {
        super();
    }

    public NoAuthException(String message) {
        super(message);
    }

    public NoAuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAuthException(Throwable cause) {
        super(cause);
    }
}

```

NoAuthExceptionResolver.java 全局异常处理器

```java
package com.zja.AuthException;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhengJa
 * @description 自定义全局异常处理器：实现异常处理器
 * @data 2019/10/22
 */
public class NoAuthExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        if (e instanceof NoAuthException && !isAjax(httpServletRequest)) {
            return new ModelAndView("NoAuthPage");
        }
        return null;
    }
    // 判断是否是 Ajax 请求
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}

```

spring-mvc.xml

```xml
	<!--配置全局异常处理器-->
    <bean class="com.zja.AuthException.NoAuthExceptionResolver"/>

```

> 和下方的拦截器搭配使用



### 6、拦截器 Interceptor

****

MyFirstInterceptor.java 自定义第一个拦截器

```java
package com.zja.MyInterceptors;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhengJa
 * @description 创建的第一个拦截器，实现HandlerInterceptor
 * @data 2019/10/22
 */
public class MyFirstInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("进入第一个拦截器 preHandle");
        return true;
    }
    // 需要注意的是，如果对应的程序报错，不一定会进入这个方法 但一定会进入 afterCompletion 这个方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        System.out.println("进入第一个拦截器 postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        System.out.println("进入第一个拦截器 afterCompletion");
    }
}

```

MySecondInterceptor.java 自定义第二个拦截器

```java
package com.zja.MyInterceptors;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhengJa
 * @description 创建的第二个拦截器，实现HandlerInterceptor
 * @data 2019/10/22
 */
public class MySecondInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("进入第二个拦截器 preHandle");
        return true;
    }
    // 需要注意的是，如果对应的程序报错，不一定会进入这个方法 但一定会进入 afterCompletion 这个方法
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        System.out.println("进入第二个拦截器 postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        System.out.println("进入第二个拦截器 afterCompletion");
    }
}


```

spring-mvc.xml 配置自定义拦截器

```xml
<!-- =====================配置自定义拦截器==================== -->
    <!--配置拦截器-->
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <mvc:exclude-mapping path="/mvc/login"/>
            <bean class="com.zja.MyInterceptors.MyFirstInterceptor"/>
        </mvc:interceptor>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <bean class="com.zja.MyInterceptors.MySecondInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>

```

HelloController.java

```java
package com.zja.controller;

import com.zja.AuthException.NoAuthException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ZhengJa
 * @description 首次 Controller类的测试
 * @data 2019/10/22
 */
@Api(value = "HelloController")
@Controller
@RequestMapping("mvc")
public class HelloController {

    @ApiOperation(value = "hello测试",httpMethod = "GET")
    @RequestMapping(value = "hello",method = RequestMethod.GET)
    public String hello(){
        return "hello";
    }

    @ApiOperation(value = "auth",httpMethod = "GET")
    @RequestMapping(value = "auth",method = RequestMethod.GET)
    private void auth() {
        throw new NoAuthException("没有对应的访问权限！");
    }
}

```



### 7、log4j2 日志配置

****

pom.xml 引入log4j2日志依赖

```xml
	<properties>
		<!--spring5.x 集成日志-->
        <log4j2.version>2.11.2</log4j2.version>
    </properties>

 	   <!--${log4j.version} 最新的2.11.2-->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>${log4j2.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>${log4j2.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-web</artifactId>
            <version>${log4j2.version}</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.2</version>
        </dependency>
        
```

log4j2.xml 文件配置，可以直接用,将log4j2.xml放到resources下

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
<!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，当设置成TRACE时，你会看到log4j2内部各种详细输出-->
<!--monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数-->

<!--推荐使用 DEBUG-->
<configuration status="INFO" monitorInterval="30">
    <!--先定义所有的appender-->
    <appenders>
        <!--输出控制台的配置-->
        <console name="Console" target="SYSTEM_OUT">
            <!--输出日志的格式-->
            <PatternLayout pattern="[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n"/>
        </console>

        <!--文件会打印出所有信息，这个log每次运行程序会自动清空，由append属性决定，这个也挺有用的，适合临时测试用-->
        <File name="log" fileName="log/test.log" append="false">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
        </File>

        <!-- 这个会打印出所有的info及以下级别的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档-->
        <RollingFile name="RollingFileInfo" fileName="${sys:user.home}/logs/info.log"
                     filePattern="${sys:user.home}/logs/$${date:yyyy-MM}/info-%d{yyyy-MM-dd}-%i.log">
            <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="debug" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n"/>

            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="10 MB"/>
            </Policies>
        </RollingFile>

        <RollingFile name="RollingFileWarn" fileName="${sys:user.home}/logs/warn.log"
                     filePattern="${sys:user.home}/logs/$${date:yyyy-MM}/warn-%d{yyyy-MM-dd}-%i.log">

            <ThresholdFilter level="warn" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100 MB"/>
            </Policies>
            <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件，这里设置了20 -->
            <DefaultRolloverStrategy max="20"/>
        </RollingFile>

        <RollingFile name="RollingFileError" fileName="${sys:user.home}/logs/error.log"
                     filePattern="${sys:user.home}/logs/$${date:yyyy-MM}/error-%d{yyyy-MM-dd}-%i.log">
            <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100 MB"/>
            </Policies>
        </RollingFile>

    </appenders>

    <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
    <loggers>

        <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
        <logger name="org.springframework" level="INFO"/>
        <logger name="org.mybatis" level="INFO"/>

        <root level="all">
            <!--输出到控制台-->
            <appender-ref ref="Console"/>
            <!--Info输出到文件-->
            <appender-ref ref="RollingFileInfo"/>
            <!--Warn输出到文件-->
            <appender-ref ref="RollingFileWarn"/>
            <!--Error输出到文件-->
            <appender-ref ref="RollingFileError"/>
        </root>

    </loggers>

</configuration>

```

web.xml配置log4j2

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--log4j2配置文件地址 -->
    <context-param>
        <param-name>log4jConfiguration</param-name>
        <param-value>classpath:log4j2.xml</param-value>
    </context-param>
    <!-- Log4j2的监听器要放在spring监听器前面 -->
    <listener>
        <listener-class>org.apache.logging.log4j.web.Log4jServletContextListener</listener-class>
    </listener>
    <filter>
        <filter-name>log4jServletFilter</filter-name>
        <filter-class>org.apache.logging.log4j.web.Log4jServletFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>log4jServletFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>INCLUDE</dispatcher>
        <dispatcher>ERROR</dispatcher>
    </filter-mapping>

    <!--配置spring前端控制器-->
    <servlet>
        <servlet-name>springMvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:META-INF/spring/spring-mvc.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>

```

log4j2 测试

```java
package com.zja.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author ZhengJa
 * @description log日志测试
 * @data 2019/10/22
 */
public class Log4j2Controller {
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
	    logger.trace("trace message");
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
        logger.fatal("fatal message");
    }

}

```



### 8、lombok 简化实体类代码

****

pom.xml 引入依赖

```xml
		<!--@Data 是lombok 包下的注解，用来生成相应的 set、get 方法，使得类的书写更为简洁 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.2</version>
        </dependency>

```

实体类：

```java
/**
 * @author ZhengJa
 * @description lombok 测试实体类
 * @data 2019/10/28
 */
@Data
public class lombokEntiry {
    private String name;
    private Integer age;
}

```



### 9、spring AOP 配置

****

pom.xml 引入依赖

```xml
	<properties>
	    <!--spring5.x 至少需要jdk1.8及以上版本-->
        <spring.version>5.0.9.RELEASE</spring.version>
	</properties>

		<!--aop 相关依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.2</version>
        </dependency>
        
```

CustomAdvice.java 自定义切面类

```java
package com.zja.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

/**
 * @author ZhengJa
 * @description 自定义切面类
 * @data 2019/10/28
 */
public class CustomAdvice {

    /**
     *前置通知
     */
    public void before(JoinPoint joinPoint) {
        //获取节点名称
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println(name + "方法调用前：获取调用参数" + Arrays.toString(args));
    }

    /**
     *后置通知 (抛出异常后不会被执行)
     */
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("后置返回通知结果" + result);
    }

    /**
     *环绕通知
     */
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知-前");
        //调用目标方法
        Object proceed = joinPoint.proceed();
        System.out.println("环绕通知-后");
        return proceed;
    }

    /**
     *异常通知
     */
    public void afterException(JoinPoint joinPoint, Exception exception) {
        System.err.println("后置异常通知:" + exception);
    }

    /**
     *后置通知 总会执行 但是不能访问到返回值
     */
    public void after(JoinPoint joinPoint) {
        System.out.println("后置通知");
    }
}

```

aop测试实体类

```java
package com.zja.entity;

import lombok.Data;

/**
 * @author ZhengJa
 * @description aop 测试实体类
 * @data 2019/10/28
 */
@Data
public class AopEntiry {
    private String name;
    private Integer age;

    @Override
    public String toString() {
        return "AopEntiry{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

```

aop测试接口

```java
package com.zja.service;

import com.zja.entity.AopEntiry;

/**
 * @author ZhengJa
 * @description aop 测试 的service接口类
 * @data 2019/10/28
 */
public interface AopService {
    Object findData(AopEntiry aopEntiry);
}

```

aop测试实现类

```java
package com.zja.service.Impl;

import com.zja.entity.AopEntiry;
import com.zja.service.AopService;

/**
 * @author ZhengJa
 * @description aop 测试 的serviceimpl实现类
 * @data 2019/10/28
 */
public class AopServiceImpl implements AopService {

    @Override
    public Object findData(AopEntiry aopEntiry) {

        System.out.println("aopEntiry"+aopEntiry);

        return "  返回值："+aopEntiry.getName();
    }
}

```

aop.xml 配置,将aop.xml文件放到resources下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启后允许使用 Spring AOP 的@AspectJ 注解 如果是纯 xml 配置 可以不用开启这个声明-->
    <aop:aspectj-autoproxy/>

    <!-- 1.配置目标对象 -->
    <bean name="aopServiceImpl" class="com.zja.service.Impl.AopServiceImpl"/>
    <!-- 2.声明切面 -->
    <bean name="myAdvice" class="com.zja.aop.CustomAdvice"/>
    <!-- 3.配置将通知织入目标对象 -->
    <aop:config>
        <!--命名切入点 关于切入点更多表达式写法可以参见 README.md-->
        <aop:pointcut expression="execution(* com.zja.service.AopService.*(..))" id="cutPoint"/>
        <aop:aspect ref="myAdvice">
            <!-- 前置通知 -->
            <aop:before method="before" pointcut-ref="cutPoint"/>
            <!-- 后置通知 如果需要拿到返回值 则要指明返回值对应的参数名称-->
            <aop:after-returning method="afterReturning" pointcut-ref="cutPoint" returning="result"/>
            <!-- 环绕通知 -->
            <aop:around method="around" pointcut-ref="cutPoint"/>
            <!-- 后置异常 如果需要拿到异常 则要指明异常对应的参数名称 -->
            <aop:after-throwing method="afterException" pointcut-ref="cutPoint" throwing="exception"/>
            <!-- 最终通知 -->
            <aop:after method="after" pointcut-ref="cutPoint"/>
        </aop:aspect>
    </aop:config>

</beans>

```

spring-mvc.xml 引入aop.xml资源

```xml
	<!--引入资源 .xml-->
    <import resource="classpath:aop.xml"/>

```

调用AopService的任意接口，都会执行以下aop：

```python
findData方法调用前：获取调用参数[AopEntiry{name='李四', age=21}]
环绕通知-前
aopEntiryAopEntiry{name='李四', age=21}
后置通知
环绕通知-后
后置返回通知结果  返回值：李四

```

> **下面为一些常见切入点表达式：**
> 任意公共方法的执行：
> execution(public * *(..))
> 
> 任何一个以 set 开头的方法的执行：
> execution(* set*(..))
> 
> AccountService 接口上任意方法的执行：
> execution(* com.zja.service.AopService.*(..))
> 
> 定义在 service 包里任意方法的执行：
> execution(* com.zja.service.*.*(..))
> 
> 定义在 service 包或者子包里任意方法的执行：
> execution(* com.zja.service..*.*(..))
> 
> 在 service 包里的任意连接点（在 Spring AOP 中只是方法执行） ：
> within(com.zja.service.*)
> 
> 在 service 包或者子包里的任意连接点（在 Spring AOP 中只是方法执行） ：
> within(com.zja.service..*)
> 
> 实现了 AccountService 接口的代理对象的任意连接点（在 Spring AOP 中只是方法执行） ：
> this(com.zja.service.AccountService)



**到此所有的配置已完成！** 下面是 配置文件的完整版！



## spring-mvc.xml 完整配置

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

    <import resource="classpath:aop.xml"/>

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
            location="classpath*:application.properties" />

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 ：/WEB-INF/jsp/和/WEB-INF/html/-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 ：.jsp和.html-->
        <property name="suffix" value=".jsp"/>
    </bean>

    <!-- =====================配置自定义拦截器==================== -->
    <!--配置拦截器-->
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <mvc:exclude-mapping path="/mvc/login"/>
            <bean class="com.zja.MyInterceptors.MyFirstInterceptor"/>
        </mvc:interceptor>
        <mvc:interceptor>
            <mvc:mapping path="/mvc/**"/>
            <bean class="com.zja.MyInterceptors.MySecondInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>

    <!--配置全局异常处理器-->
    <bean class="com.zja.AuthException.NoAuthExceptionResolver"/>

    <!-- =================== springmvc文件上传和下载 ================== -->
    <!--配置文件上传-->
    <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
        <property name="maxUploadSize" value="102400000"/>
        <property name="maxUploadSizePerFile" value="10240000"/>
        <property name="defaultEncoding" value="utf-8"/>
    </bean>

    <!-- =================== swagger2 config ================== -->
    <bean class="com.zja.config.Swagger2Config"/>
    <mvc:resources mapping="swagger-ui.html" location="classpath:/META-INF/resources/"/>
    <mvc:resources mapping="/webjars/**" location="classpath:/META-INF/resources/webjars/"/>

</beans>

```



## web.xml完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!--log4j2配置文件地址 -->
    <context-param>
        <param-name>log4jConfiguration</param-name>
        <param-value>classpath:log4j2.xml</param-value>
    </context-param>
    <!-- Log4j2的监听器要放在spring监听器前面 -->
    <listener>
        <listener-class>org.apache.logging.log4j.web.Log4jServletContextListener</listener-class>
    </listener>
    <filter>
        <filter-name>log4jServletFilter</filter-name>
        <filter-class>org.apache.logging.log4j.web.Log4jServletFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>log4jServletFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>INCLUDE</dispatcher>
        <dispatcher>ERROR</dispatcher>
    </filter-mapping>

    <!--配置spring前端控制器-->
    <servlet>
        <servlet-name>springMvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:META-INF/spring/spring-mvc.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

</web-app>

```



## pom.xml 完整依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.zja</groupId>
    <artifactId>spring5x-springmvc</artifactId>
    <packaging>war</packaging>

    <name>spring5x-springmvc</name>

    <!--说明：spring5.x-base模块是spring5.x基础框架，其它模块都是以此模块为基础扩展的-->
    <properties>
        <!--spring5.x 至少需要jdk1.8及以上版本-->
        <spring.version>5.0.9.RELEASE</spring.version>
        <!--jdk必须 >=1.8-->
        <jdk.version>1.8</jdk.version>
        <!--maven 版本-->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.plugin.version>3.6.0</maven.compiler.plugin.version>
        <mavne.surefire.plugin.version>2.19.1</mavne.surefire.plugin.version>
        <maven-war-plugin.version>2.6</maven-war-plugin.version>
        <servlet.version>4.0.1</servlet.version>

        <!--spring5.x集成swagger2-->
        <springfox.version>2.9.2</springfox.version>
    </properties>

    <dependencies>
        <!--spring核心包——Start-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-oxm</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <!--aop 相关依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.2</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>
        <!-- spring核心包——End -->

        <!--servlet-api  web层-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet.version}</version>
            <scope>provided</scope>
        </dependency>

        <!--spring 的文件上传类 CommonsMultipartResolver 依赖了这个包-->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.3</version>
        </dependency>

        <!--@Data 是lombok 包下的注解，用来生成相应的 set、get 方法，使得类的书写更为简洁 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.2</version>
        </dependency>

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

        <!-- 数据校验依赖包 -->
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>6.0.13.Final</version>
        </dependency>
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>


    </dependencies>

    <build>
        <finalName>spring5x-springmvc</finalName>
        <plugins>
            <!--maven的编译插件-->
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven.compiler.plugin.version}</version>
                <configuration>
                    <!--开发版本-->
                    <source>${jdk.version}</source>
                    <!--.class文件版本-->
                    <target>${jdk.version}</target>
                    <!--打包后的编码-->
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
            <!--打包跳过测试-->
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${mavne.surefire.plugin.version}</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>


```


**基础搭建已经可以结束！！！**



## github 地址：
* [https://github.com/zhengjiaao/spring5x](https://github.com/zhengjiaao/spring5x)


## 博客地址
* 简书：https://www.jianshu.com/u/70d69269bd09
* 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts




