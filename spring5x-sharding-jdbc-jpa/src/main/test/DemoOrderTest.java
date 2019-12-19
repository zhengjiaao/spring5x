import com.zja.dao.OrderJpaRepositories;
import com.zja.entity.Order;
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
 * Date: 2019-12-17 15:00
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoOrderTest {

    @Autowired
    private OrderJpaRepositories jpaRepositories;

    @Test
    public void saveAll() {

        List<Order> orders = new ArrayList<>();

        for (int i=1;i<=10;i++){
            Order order = new Order();
            order.setOrderId(i+0L);
            order.setUserId(i);
            order.setStatus("true");
            order.setAddressId(i+0L);

            orders.add(order);
        }

        List<Order> jpas = this.jpaRepositories.saveAll(orders);
        System.out.println(jpas);
    }

    @Test
    public void save(){
        Order order = new Order();
        //order_id为偶数，插入t_order_1
        //order_id为奇数，插入t_order_2
        order.setOrderId(1);

        order.setUserId(32);
        order.setStatus("true");
        order.setAddressId(32L);
        Order save = this.jpaRepositories.save(order);
        System.out.println(save);
    }

    @Test
    public void Sort(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        List<Order> orderList = jpaRepositories.findAll(sort);
        System.out.println(orderList);
    }

    @Test
    public void Pageable(){
        Sort sort =new Sort(Sort.Direction.ASC,"userId");
        Pageable pageable = PageRequest.of(0,5,sort);
        Page<Order> orderPage = jpaRepositories.findAll(pageable);
        List<Order> orderList = orderPage.getContent();
        System.out.println(orderList);
    }
}
