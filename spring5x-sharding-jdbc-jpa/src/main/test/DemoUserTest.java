import com.zja.dao.UserJpaRepositories;
import com.zja.entity.Order;
import com.zja.entity.User;
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
import java.util.List;
import java.util.Optional;

/**
 * Date: 2019-12-18 9:34
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoUserTest {

    @Autowired
    private UserJpaRepositories jpaRepositories;

    @Test
    public void saveAll(){
        List<User> users = new ArrayList<>();

        for (int i=1;i<=10;i++){
            User user = new User();
            user.setUserId(i+0L);
            user.setUserName("李四"+i);
            user.setUserNamePlain("a"+i);
            user.setPwd("pwd"+i);
            user.setAssistedQueryPwd(""+i);

            users.add(user);
        }

        List<User> list = this.jpaRepositories.saveAll(users);
        System.out.println(list);
    }


    @Test
    public void Sort(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        List<User> userList = jpaRepositories.findAll(sort);
        System.out.println(userList);
    }

    @Test
    public void Pageable(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        Pageable pageable = PageRequest.of(0,5,sort);
        Page<User> userPage = jpaRepositories.findAll(pageable);
        List<User> userList = userPage.getContent();
        System.out.println(userList);
    }

}
