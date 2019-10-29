## SpringWork技术整合

### 技术指南

Spring的一个最大的目的就是使JAVA EE开发更加容易。同时，Spring之所以与Struts、Hibernate等单层框架不同，是因为Spring致力于提供一个以统一的、高效的方式构造整个应用，并且可以将单层框架以最佳的组合揉和在一起建立一个连贯的体系。

Maven多模块项目,适用于一些比较大的项目，通过合理的模块拆分，实现代码的复用，便于维护和管理。尤其是一些开源框架，也是采用多模块的方式，提供插件集成，用户可以根据需要配置指定的模块。

这里，我们可以使用maven多模块功能组织项目，将不同的组件使用Spring统一管理。

#### 概览

SpringWork项目整合了Spring常用的一些关键性技术，使用组件化的方式自动装配，可以快速生成基础项目框架。

源码为初始源代码，部分组件提供了易用性整合，提高了可读性和易用性，部分仍需继续改造。

项目包含
	- Spring基本功能。
	- Spring Mvc，Spring系列视图控制层
	- Mail， 邮件服务，组件进行了易用性整合，统一了邮件内文本、图片、附件等的发送方式，简单易用。
	- WebSocket, HTML5定义了WebSocket协议, 这里做了服务端简单的IM聊天功能。
	- ... 

#### Spring + SpringMvc

##### 官方文档

[Spring](https://docs.spring.io/spring/docs/5.0.12.RELEASE/spring-framework-reference/core.html#spring-core)

[Spring Mvc](https://docs.spring.io/spring/docs/5.0.12.RELEASE/spring-framework-reference/web.html#spring-web)

##### 技术方案

[查看详情](springwork-web/README.md)

#### Spring + Mail

##### 官方文档

[Spring Email](https://docs.spring.io/spring/docs/5.0.12.RELEASE/spring-framework-reference/integration.html#mail-introduction)

#### Spring + Mybatis

##### 官方文档

[Spring Mybatis](http://www.mybatis.org/mybatis-3/zh/index.html)



#### 文档继续维护中...

