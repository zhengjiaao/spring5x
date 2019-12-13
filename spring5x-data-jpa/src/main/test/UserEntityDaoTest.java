import com.zja.dao.UserEntityDao;
import com.zja.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Date: 2019-12-12 17:49
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class UserEntityDaoTest {

    @Autowired
    private UserEntityDao userEntityDao;

    //批量保存数据
    @Test
    public void saveAll(){
        List<UserEntity> userEntities = new ArrayList<>();

        for (int i=1;i<10;i++){

            UserEntity userEntity = new UserEntity();
            userEntity.setId(i+0L);
            userEntity.setUserName("李四"+i);
            userEntity.setAge(14+i);
            userEntity.setCreateTime(new Date());
            userEntity.setUpdateTime(new Date());

            userEntities.add(userEntity);
        }

        List<UserEntity> jpas = this.userEntityDao.saveAll(userEntities);
        System.out.println(jpas);
    }

    //查询全部数据
    @Test
    public void findAll(){
        List<UserEntity> userEntityList = this.userEntityDao.findAll();
        System.out.println(userEntityList);
    }

    //按实体类属性更新(修改)
    @Test
    public void findByUpdateUserName(){
        int updateResult = this.userEntityDao.findByUpdateUserName("李四2","张三");
        System.out.println(updateResult);
        //按实体类属性用户名like模糊查询
        UserEntity byUserName = this.userEntityDao.findUserByUserName("张三");
        System.out.println(byUserName);
    }

    //按实体类属性age查询
    @Test
    public void findUserByAge(){
        List<UserEntity> userEntityList = this.userEntityDao.findUserByAge(15);
        System.out.println(userEntityList);
    }

    //like模糊查询
    @Test
    public void findUserByLikeUserName(){
        List<UserEntity> userEntityList = this.userEntityDao.findUserByLikeUserName("李四%");
        System.out.println(userEntityList);
    }

    //排序并分页
    @Test
    public void pageable(){
        Sort sort =new Sort(Sort.Direction.DESC,"age");
        Pageable pageable = PageRequest.of(2,3,sort);
        Page<UserEntity> entityPage = userEntityDao.findAll(pageable);
        List<UserEntity> userEntityList = entityPage.getContent();
        System.out.println(userEntityList);
        System.out.println("每页几条数据："+entityPage.getSize());
        System.out.println("当前页："+entityPage.getNumber());
        System.out.println("排序方式："+entityPage.getSort());
        System.out.println("总数据条数："+entityPage.getTotalElements());
        System.out.println("总页数"+entityPage.getTotalPages());
    }

    //排序不分页
    @Test
    public void sort(){
        Sort sort =new Sort(Sort.Direction.ASC,"age");
        List<UserEntity> userEntityList = userEntityDao.findAll(sort);
        System.out.println(userEntityList);
    }

    //批量删除数据
    @Test
    public void deleteInBatch(){
        List<UserEntity> userEntities = new ArrayList<>();

        for (int i=1;i<5;i++){

            UserEntity userEntity = new UserEntity();
            userEntity.setId(i+0L);
            userEntity.setUserName("李四"+i);
            userEntity.setAge(14+i);
            userEntity.setCreateTime(new Date());
            userEntity.setUpdateTime(new Date());

            userEntities.add(userEntity);
        }
        //实际按id批量删除
        userEntityDao.deleteInBatch(userEntities);
        //查询删除后的数据
        List<UserEntity> userEntityList = userEntityDao.findAll();
        System.out.println(userEntityList);
    }

    //删除全部数据
    @Test
    public void deleteAllInBatch(){
        userEntityDao.deleteAllInBatch();
        //查询删除后的数据
        List<UserEntity> userEntityList = userEntityDao.findAll();
        System.out.println(userEntityList);
    }
}
