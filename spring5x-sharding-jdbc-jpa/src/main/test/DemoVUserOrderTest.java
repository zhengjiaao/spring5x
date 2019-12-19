import com.zja.dao.VUserOrderJpaRepositories;
import com.zja.entity.VUserOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Date: 2019-12-18 9:57
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoVUserOrderTest {

    @Autowired
    private VUserOrderJpaRepositories jpaRepositories;

    @Test
    public void findAll(){
        List<VUserOrder> vUserOrderList = jpaRepositories.findAll();
        System.out.println(vUserOrderList);
    }
}
