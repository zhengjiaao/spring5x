# spring5.x-email

[TOC]

spring5.x-email 此模块是从spring5x-base 基础模块扩展过来的

spring5x-base模块是一个非常干净的spring5.x+springMVC架构

如果没有搭建spring5x-base模块，请参考 [spring5x-base模块搭建](spring5.x-base.md)



## 搭建项目

**基于spring5x-base 基础模块 新增功能：**

- spring 邮件发送 配置



### spring 邮件发送 配置

****

pom.xml

```xml
		<!--邮件发送依赖包,根据自己的spring5.x的版本-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>javax.mail</groupId>
            <artifactId>mail</artifactId>
            <version>1.4.7</version>
        </dependency>
        <!--模板引擎-->
        <!--这里采用的是 beetl,beetl 性能很卓越并且功能也很全面 官方文档地址 <a href="http://ibeetl.com/guide/#beetl">-->
        <dependency>
            <groupId>com.ibeetl</groupId>
            <artifactId>beetl</artifactId>
            <version>2.9.7</version>
        </dependency>

```

>注： beetl 是模板引擎，其性能比较优异，官网是介绍其性能 6 倍于 freemaker，当然也可以换成其他模板引擎（ 如 freemarker，thymeleaf）



spring-email.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--在这里可以声明不同的邮件服务器主机，通常是 SMTP 主机,而具体的用户名和时授权码则建议在业务中从数据库查询-->
    <bean id="qqMailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
        <!--qq 邮箱配置 <a href="https://service.mail.qq.com/cgi-bin/help?subtype=1&no=167&id=28"> -->
        <property name="host" value="smtp.qq.com"/>
        <property name="port" value="587"/>
    </bean>

    <!--配置 beetle 模板引擎 如果不使用模板引擎，以下的配置不是必须的-->
    <bean id="resourceLoader" class="org.beetl.core.resource.ClasspathResourceLoader">
        <!--指定加载模板资源的位置 指定在 classpath:template 下-->
        <constructor-arg name="root" value="template"/>
    </bean>
    <!--beetl 配置  这里采用默认的配置-->
    <bean id="configuration" class="org.beetl.core.Configuration" init-method="defaultConfiguration"/>
    <bean id="groupTemplate" class="org.beetl.core.GroupTemplate">
        <constructor-arg name="loader" ref="resourceLoader"/>
        <constructor-arg name="conf" ref="configuration"/>
    </bean>

</beans>

```

>注：模板路径 template 具体在resources/template ，作用是放置模板



EmailUtil.java 简单工具类

```java
package com.zja.util;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * @author ZhengJa
 * @description 邮件发送基本类
 * @data 2019/10/30
 */
@Component
public class EmailUtil {

    @Autowired
    private JavaMailSenderImpl qqMailSender;

    @Autowired
    private GroupTemplate groupTemplate;

    /**
     * 发送简单邮件
     * 在 qq 邮件发送的测试中，测试结果表明不管是简单邮件还是复杂邮件都必须指定发送用户，
     * 且发送用户已经授权不然都会抛出异常: SMTPSendFailedException 501 mail from address must be same as authorization user
     * qq 的授权码 可以在 设置/账户/POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV 服务 中开启服务后获取
     */
    public void sendTextMessage(String from, String authWord, String to, String subject, String content) {
        // 设置发送人邮箱和授权码
        qqMailSender.setUsername(from);
        qqMailSender.setPassword(authWord);
        // 实例化消息对象
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            // 发送消息
            this.qqMailSender.send(msg);
            System.out.println("发送邮件成功");
        } catch (MailException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 发送带附件的邮件
     */
    public void sendEmailWithAttachments(String from, String authWord, String to,
                                         String subject, String content, Map<String, File> files) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为 true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            // 传入附件
            for (Map.Entry<String, File> entry : files.entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (javax.mail.MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }


    /**
     * 发送带内嵌资源的邮件
     */
    public void sendEmailWithInline(String from, String authWord, String to,
                                    String subject, String content, File file) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为 true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 使用 true 标志来指示包含的文本是 HTML 固定格式资源前缀 cid:
            helper.setText("<html><body><img src='cid:image'></body></html>", true);
            // 需要先指定文本 再指定资源文件
            FileSystemResource res = new FileSystemResource(file);
            helper.addInline("image", res);
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (javax.mail.MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 使用模板邮件
     */
    public void sendEmailByTemplate(String from, String authWord, String to,
                                    String subject, String content) {
        try {
            Template t = groupTemplate.getTemplate("emailtemplate.html");
            t.binding("subject", subject);
            t.binding("content", content);
            String text = t.render();
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 指定 utf-8 防止乱码
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 为 true 时候 表示文本内容以 html 渲染
            helper.setText(text, true);
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (javax.mail.MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

}


```

>注：需要 引入下方的 emailtemplate.html 模板



emailtemplate.html 模板

```
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <h1>邮件主题:<span style="color: chartreuse"> ${subject}</span></h1>
    <h4 style="color: blueviolet">${content}</h4>
</body>
</html>

```

>1、位置：放在在resources/template下
>
>2、使用：在 EmailUtil.java 简单工具类 中配置



spring-mvc.xml

```xml
	<!--email-->
    <import resource="spring-email.xml"/>

```

>注：需要在spring-mvc.xml中引入资源spring-email.xml



SendEmail.java  测试类

```java
import com.zja.util.EmailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhengJa
 * @description 邮件发送单元测试
 * @data 2019/10/30
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:META-INF/spring/spring-mvc.xml")
public class SendEmail {

    @Autowired
    private EmailUtil emailUtil;

    // 发送方邮箱地址
    private static final String from = "1263598336@qq.com";
    // 发送方邮箱地址对应的授权码,要改用自己的（发送方邮箱）
    private static final String authWord = "yoypgcyowgofieas";
    // 接收方邮箱地址
    private static final String to = "953649948@qq.com";


    /**
     * 简单邮件测试
     */
    @Test
    public void sendMessage() {
        emailUtil.sendTextMessage(from, authWord, to, "spring 简单邮件", "Hello Spring Email!");
    }

    /**
     * 发送带附件的邮件
     */
    @Test
    public void sendComplexMessage() {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("image1.jpg", new File("D:\\FileTest\\图\\cube01.bmp"));
        fileMap.put("image2.jpg", new File("D:\\FileTest\\图\\头像.jpg"));
        emailUtil.sendEmailWithAttachments(from, authWord, to, "spring 多附件邮件"
                , "Hello Spring Email!", fileMap);
    }

    /**
     * 发送内嵌资源的邮件
     */
    @Test
    public void sendEmailWithInline() {
        emailUtil.sendEmailWithInline(from, authWord, to, "spring 内嵌资源邮件"
                , "Hello Spring Email!", new File("D:\\FileTest\\图\\头像.jpg"));
    }

    /**
     * 发送模板邮件
     */
    @Test
    public void sendEmailByTemplate() {
        emailUtil.sendEmailByTemplate(from, authWord, to,
                "spring 模板邮件", "Hello Spring Email!");
    }

}

```

>注：
>
>1、邮箱发送和接收请改用自己的，
>
>2、**邮箱发送方的授权码** 获取参考：https://www.jianshu.com/p/9efaff9e9437
>
>3、内容图片资源路径改用自己本地的

**到此所有的配置已完成！** 下面时配置文件的完整版！



## pom.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zja</groupId>
    <artifactId>spring5x-email</artifactId>
    <packaging>war</packaging>

    <name>spring5x-email</name>

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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>${spring.version}</version>
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
        <!--spring核心包——End-->

        <!--servlet-api  web层-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet.version}</version>
            <scope>provided</scope>
        </dependency>

        <!--jackson  默认采用 Jackson 将对象进行序列化和反序列化-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.4</version>
            <exclusions>
                <exclusion>
                    <artifactId>jackson-annotations</artifactId>
                    <groupId>com.fasterxml.jackson.core</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.4</version>
        </dependency>

        <!--邮件发送依赖包，上面已经有引入了-->
        <!--<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring.version}</version>
        </dependency>-->

        <dependency>
            <groupId>javax.mail</groupId>
            <artifactId>mail</artifactId>
            <version>1.4.7</version>
        </dependency>
        <!--模板引擎-->
        <!--这里采用的是 beetl,beetl 性能很卓越并且功能也很全面 官方文档地址 <a href="http://ibeetl.com/guide/#beetl">-->
        <dependency>
            <groupId>com.ibeetl</groupId>
            <artifactId>beetl</artifactId>
            <version>2.9.7</version>
        </dependency>

        <!--测试相关依赖-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>


    </dependencies>

    <build>
        <finalName>spring5x-email</finalName>
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



## spring-xml.xml 完整配置

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
            location="classpath:application.properties" />

    <!--email-->
    <import resource="spring-email.xml"/>

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 ：/WEB-INF/jsp/和/WEB-INF/html/-->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 ：.jsp和.html-->
        <property name="suffix" value=".jsp"/>
    </bean>

</beans>

```



## web.xml 完整配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

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

    <!--Encoding configuration-->
    <filter>
        <filter-name>encoding</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encoding</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>

```



**到此搭建已经完成！！！**





## github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)




## 博客地址

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金： https://juejin.im/user/5d82daeef265da03ad14881b/posts