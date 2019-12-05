
# spring5x-hibernate5-cfg

[toc]

spring5x-hibernate5-cfg 此模块是从spring5x-hibernate5-base 基础模块扩展过来的
spring5x-hibernate5-base模块是一个hibernate5基础架构，可参考[spring5x-hibernate5-base 基础模块]()

## 搭建项目

**基于spring5x-hibernate5-base 基础模块 新增功能：**

- 1、关联关系(如多对多/一对多等)
- 2、解决noSession和懒加载无限循环的几种方式


### 1、关联关系(如多对多/一对多等)

****

manyToMany(多对多)
实体类
```java
/**
 * 学生
 * Hibernate获取数据java.lang.StackOverflowError 原因：因为在重写toString()方法时，把关联的属性也放入到toString方法中了，去掉就可以了
 */
public class Student {
	private int stuId;
	private String stuName;
	//老师集合
	private Set<Teacher> teachers;

	public Student() {
	}
	public int getStuId() {
		return stuId;
	}
	public void setStuId(int stuId) {
		this.stuId = stuId;
	}
	public String getStuName() {
		return stuName;
	}
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	//不序列化
	@JsonBackReference
	public Set<Teacher> getTeachers() {
		return teachers;
	}
	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}
}

/**
 * 老师
 * Hibernate获取数据java.lang.StackOverflowError 原因：因为在重写toString()方法时，把关联的属性也放入到toString方法中了，去掉就可以了
 */
public class Teacher{
	private int teaId;
	private String teaName;
	// 学生集合
	private Set<Student> students;

	public Teacher() {
	}

	public int getTeaId() {
		return teaId;
	}

	public void setTeaId(int teaId) {
		this.teaId = teaId;
	}

	public String getTeaName() {
		return teaName;
	}

	public void setTeaName(String teaName) {
		this.teaName = teaName;
	}

	@JsonBackReference
	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

}

```
*.hbm.xml *与实体类名称相同
```xml
<!--Student.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping>
    <class name="com.zja.entity.manytoMany.Student" table="t_student">
        <id name="stuId" type="int">
            <column name="stuId" />
            <generator class="increment" />
        </id>
        <property name="stuName" type="java.lang.String">
            <column name="stuName" />
        </property>
        <!-- 这里的table是指连接表的名称 -->
        <!--lazy:proxy(使用代理并延迟加载),no-proxy(不适用代理并延迟加载),false(立即加载)-->
        <!--inverse:false属性说明Student实体类是主控方，负责维护关系表。只有一方维护关系，否则会造成重复更新-->
        <!--cascade:指明级联操作的类型-->
        <set name="teachers" table="t_stu_tea" cascade="save-update,delete" inverse="false" lazy="true">
            <!-- 这个是关联表的字段名，同时是Teacher的外键 -->
            <key>
                <column name="stuId"/>
            </key>
            <many-to-many class="com.zja.entity.manytoMany.Teacher" column="teaId"/>
        </set>
    </class>
</hibernate-mapping>

<!--Teacher.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping>
    <class name="com.zja.entity.manytoMany.Teacher" table="t_teacher">
        <id name="teaId" type="int">
            <column name="teaId" />
            <generator class="increment" />
        </id>
        <property name="teaName" type="java.lang.String">
            <column name="teaName" />
        </property>
        <!-- 这里的table是指连接表(中间表)的名称 -->
        <!--lazy:proxy(使用代理并延迟加载),no-proxy(不适用代理并延迟加载),false(立即加载)-->
        <!--inverse:true属性说明Teacher实体类是被控方，不负责维护关系表。只有一方维护关系，否则会造成重复更新-->
        <set name="students" table="t_stu_tea" cascade="save-update,delete" inverse="true" lazy="true">
            <!-- 这个是关联表的字段名，同时是Teacher的外键 -->
            <key column="teaId" />
            <many-to-many class="com.zja.entity.manytoMany.Student" column="stuId"/>
        </set>
    </class>
</hibernate-mapping>

```

oneToMany(一对多、多对一)
```java
/**
 * Date: 2019-11-27 14:19
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：关键点是通过部门实体类维护到员工的实体类
 */
@Getter
@Setter
public class Dept {
    private int deptId;// 部门编号
    private String deptName;// 部门名称

    //@JSONField(serialize=false)  //被注解的字段不会被序列化
    //@JsonIgnore  //被注解的字段不会被序列化
    @JsonBackReference //在序列化时，@JsonBackReference的作用相当于@JsonIgnore
    private Set<Employee> emps;// 部门对应多个员工，即一对多的关系
}

/**
 * Date: 2019-11-27 14:20
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@Getter
@Setter
public class Employee {
    private int empId;// 员工的编号
    private String empName;// 员工的名称
    private double salary;// 员工的薪资

    //@JSONField(serialize=false)  //被注解的字段不会被序列化
    //@JsonIgnore //被注解的字段不会被序列化
    @JsonBackReference //尽量放到get方法上，在序列化时，@JsonBackReference的作用相当于@JsonIgnore
    private Dept dept;// 员工和部门的关系
}

```
*.hbm.xml *与实体类名称相同
```xml
<!--Dept.hbm.xml-->
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping package="com.zja.entity.oneToMany">
    <class name="Dept" table="t_dept">
        <!-- 第一写主键映射 -->
        <id name="deptId" column="deptId">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment" />
        </id>
        <!-- 第二写其他字段映射 -->
        <property name="deptName" column="deptName"
                  length="20"	type="string" />
        <!--
            第三写其他映射，比如这里的set集合映射，set集合映射主要有以下几点：
                1 实体类申明的集合属性属性（name）
                2 集合属性对应的表（table）
                3 指定集合表的外键字段名称（key中的column）
                4 集合对象对应的实体类（noe-to-many中的class）
        -->
        <!--一对多(一个部门对应多个员工)-->
        <set name="emps" table="t_employee">
            <!--column指定了员工表的外键字段名称 -->
            <key column="deptId"/>
            <!-- class由于上面已经写了包名，这里直接使用即可 -->
            <one-to-many class="Employee" />
        </set>
    </class>
</hibernate-mapping>

<!--Employee.hbm.xml-->
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping package="com.zja.entity.oneToMany">
    <class name="Employee" table="employee">
        <!-- 主键映射 -->
        <id name="empId" column="empId">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment" />
        </id>
        <!-- 其他字段映射 -->
        <property name="empName" column="empName" length="20" type="string"/>
        <property name="salary" column="salary" type="double"/>
        <!--
            多对一的映射配置：多个员工对应一个部门
            name是实体类中申明的属性；
            column外键字段名称，对应多的一方的数据库表中的外键字段名；
            class对应的部门实体类；
        -->
        <many-to-one name="dept" column="deptId" class="Dept"/>
    </class>

</hibernate-mapping>

```

oneToOne(基于外键：一对一)
```java
/**
 * 档案实体 resume:user 基于外键的一对一 [多对一 (unique=true)]
 */
@Getter
@Setter
public class Resume {
	
	private int resId;
	private String resName;

	//用户
	private UserOne user;
}

/**
 * 用户实体 用户对档案 基于外键的一对一
 */
@Getter
@Setter
public class UserOne {
	
	private int userId;
	private String userName;

	//档案
	private Resume resume;
}

```
*.hbm.xml *与实体类名称相同
```xml
<!--Resume.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<!-- 基于外键的一对一 ，和主键关联的区别是：主键策略正常配置，one-to-one改为many-to-one,
     注意保存顺序，先保存主的一方（用户），可以减少update-->
<hibernate-mapping>
    <class name="com.zja.entity.oneToOne.Resume" table="RESUME">
        <id name="resId" column="RESID" type="int">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment"/>
        </id>
        <property name="resName" column="RESNAME" type="java.lang.String"/>

        <!--多对一：档案对用户
        	一对一配置，name是实体类UserOne的属性
        	column是数据库外键字段名
        	由于是多对一的特例，所以需要加unique属性
        -->
        <many-to-one name="user" class="com.zja.entity.oneToOne.UserOne" cascade="all">
        <!-- 指定列不能重复 -->
        <column name="user_id" unique="true"/>
        </many-to-one>

    </class>
</hibernate-mapping>

<!--UserOne.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<!-- 基于外键的一对一 ，和主键关联的区别是：新增property-ref属性，
     如果没有指定此属性会使用对方关联类的主键来跟本类的主键比较，这里要注意不是关联表中的外键字段名，是对象属性-->
<hibernate-mapping>
    <class name="com.zja.entity.oneToOne.UserOne" table="USERONE">
        <id name="userId" column="USERID" type="int">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment"/>
        </id>
        <property name="userName" column="USERNAME" type="java.lang.String"/>

        <!--
           name:是实体类中引入的属性名
           property-ref:指定关联类的属性名(档案所属的学生)，
           这个属性将会和本类的主键相对应
       -->
        <one-to-one name="resume" class="com.zja.entity.oneToOne.Resume" property-ref="user"/>

    </class>
</hibernate-mapping>

```

oneToOnePK(基于主键：一对一)
```java
/**
 * 档案实体  resume:user 基于主键的一对一
 */
@Getter
@Setter
public class ResumePk {
	
	private int resId;
	private String resName;

	//用户
	@JsonBackReference
	private UserPk user;
}

/**
 * 用户实体  UserPk:ResumePk 基于主键的一对一
 */
@Getter
@Setter
public class UserPk {
	
	private int userId;
	private String userName;

	//档案
	@JsonBackReference
	private ResumePk resume;
}

```
*.hbm.xml *与实体类名称相同
```xml
<!--ResumePk.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<!--基于主键的一对一-->
<hibernate-mapping>
    <class name="com.zja.entity.oneToOnePk.ResumePk" table="pk_resume">
        <id name="resId" column="RESID" type="int">
            <!-- 档案表是外键 generator -->
            <generator class="foreign">
            <!-- 引用one-to-one 关联属性名称 -->
            <!-- 重点在这里，因为主键跟外键是同一个，直接在此申明该主键就是外键 -->
            <param name="property">user</param>
            </generator>
        </id>
        <property name="resName" column="RESNAME" type="java.lang.String"/>

        <!--
        1、constrained只能在one-to-one的映射中使用，（一般在主表的映射中，有外键的那个表）。
        如果constrained=true， 则表明存在外键与关联表对应，并且关联表中肯定存在对应的键与其对应，
        另外该选项最关键的是影响save和delete的先后顺序。例如增加的时候，如果constainted=true,则会先增加关联表，然后增加本表。删除的时候反之。
        2、one-to- one的双向关联中，必须设置constrained=true，要不然会有重复数据读。
        3、one-to-one的单向关联中，如果constrained=false，则会在查询时就全部取出来，用left outer join的方式。
        如果constrained=true，hibernate即会延迟加载sql，只把主表的查出来，等有用到关联表的再发sql取。-->
        <one-to-one name="user" class="com.zja.entity.oneToOnePk.UserPk" constrained="true"/>
    </class>
</hibernate-mapping>

<!--UserPk.hbm.xml-->
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
<!--基于主键的一对一-->
<hibernate-mapping>
    <class name="com.zja.entity.oneToOnePk.UserPk" table="pk_user">
        <id name="userId" column="USERID" type="int">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment"/>
        </id>
        <property name="userName" column="USERNAME" type="java.lang.String"/>

        <one-to-one name="resume" class="com.zja.entity.oneToOnePk.ResumePk" cascade="all"/>
    </class>
</hibernate-mapping>

```

sinceMapping(自关联：多对一、一对多)
```java
/**
 * Date: 2019-11-25 13:24
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：Hibernate5 映射之自身关联
 */
@Data
@NoArgsConstructor
public class Menu implements Serializable {
    private int menuId;
    private String menuName;
    // 菜单和子菜单是一对多，子引入父类对象
    private Menu parentMenu;
    // 这里引入子类对象集合
    private Set<Menu> childMenus = new HashSet<>();
}

```
*.hbm.xml *与实体类名称相同
```xml
<!--Menu.hbm.xml-->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<!-- Generated 2018-7-31 16:16:18 by Hibernate Tools 3.5.0.Final -->
<!--package解决每个实体类都要写全路径-->
<hibernate-mapping>
    <class name="com.zja.entity.sinceMapping.Menu" table="t_menu">
        <id name="menuId" column="menuId" type="int">
            <!--在内存中生成主键，不依赖于底层的数据库，因此可以跨数据库,首次从数据库取主键最大的值-->
            <generator class="increment" />
        </id>
        <property name="menuName" type="java.lang.String">
            <column name="menuName" />
        </property>

        <!--###注意：自关联查询，不能同时开启一对多和多对一，会发生死循环###-->

        <!--多对一-->
        <!--表中会生成parentId字段;lazy:proxy(使用代理并延迟加载),no-proxy(不适用代理并延迟加载),false(立即加载)-->
        <many-to-one name="parentMenu" class="com.zja.entity.sinceMapping.Menu" column="parentId" lazy="false"/>
        <!--一对多-->
        <!--<set name="childMenus" cascade="all" lazy="false">
            <key column="parentId" />
            <one-to-many class="com.zja.entity.sinceMapping.Menu"/>
        </set>-->

    </class>
</hibernate-mapping>

```

### 2、解决noSession和懒加载无限循环的几种方式
- https://www.jianshu.com/p/ecfb11509a46


## 具体实现操作代码参考 github 地址：

- [https://github.com/zhengjiaao/spring5x](https://links.jianshu.com/go?to=https%3A%2F%2Fgithub.com%2Fzhengjiaao%2Fspring5x)


## 博客地址:

- 简书：https://www.jianshu.com/u/70d69269bd09
- 掘金：https://juejin.im/user/5d82daeef265da03ad14881b/posts

