import com.zja.dao.AddressJpaRepositories;
import com.zja.entity.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019-12-18 16:35
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoAddressTest {

    @Autowired
    private AddressJpaRepositories jpaRepositories;

    @Test
    public void saveAll(){
        List<Address> addresses = new ArrayList<>();
        for (int i=1;i<=10;i++){
            Address address = new Address();
            address.setAddressId(i+0L);
            address.setAddressName("Name"+i);
            addresses.add(address);
        }
        //由于t_address是广播表，当插入的数据，会存到每个库中的t_address表中，每张表都有完整的表数据
        List<Address> addressList = jpaRepositories.saveAll(addresses);
        System.out.println(addressList);
    }

    @Test
    public void findAll(){
        List<Address> addressList = this.jpaRepositories.findAll();
        System.out.println(addressList);
    }
}
