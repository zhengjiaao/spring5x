import com.zja.controller.HelloController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Date: 2020-01-06 13:46
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：第一个测试用例
 */
@Slf4j
public class HelloControllerTest extends BaseTest {

    @Resource
    private HelloController helloController;

    @Before
    public void first() {
        this.setClassName("HelloController");
        this.setLog(LoggerFactory.getLogger(HelloControllerTest.class));
    }

    @Test
    public void hello() {
        try {
            String hello = this.helloController.hello2();
            this.sayStr(hello, "hello2");
        } catch (Exception e) {
            this.sayException(e, "hello2");
        }
    }

}
