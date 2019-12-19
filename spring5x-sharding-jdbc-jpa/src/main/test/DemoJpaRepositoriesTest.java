import com.zja.dao.DemoJpaRepositories;
import com.zja.entity.DemoJpa;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2019-12-12 17:18
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:META-INF/spring/spring-common.xml"})
public class DemoJpaRepositoriesTest {

    @Autowired
    private DemoJpaRepositories repositories;

    @Test
    public void saveAll() {

        List<DemoJpa> demoJpas = new ArrayList<>();

        for (int i=1;i<10;i++){
            DemoJpa demoJpa = new DemoJpa();
            demoJpa.setId(i);
            demoJpa.setAge(14+i);
            demoJpa.setEmail("126@qq.com");
            demoJpa.setFirstName("May"+i);
            demoJpa.setLastName("Eden"+i);
            demoJpas.add(demoJpa);
        }

        List<DemoJpa> jpas = this.repositories.saveAll(demoJpas);
        System.out.println(jpas);
    }

    @Test
    public void findByFirstNameAndLastName() {
        DemoJpa demoJpa = repositories.findByFirstNameAndLastName("May1", "Eden1");
        System.out.println(demoJpa);
        Assert.assertEquals(demoJpa.getFirstName(),"May1");
    }

    @Test
    public void findByLastNameOrFirstName() {
        DemoJpa demoJpa = repositories.findByLastNameOrFirstName("Eden1", "May1");
        System.out.println(demoJpa);
        //Assert.assertNotEquals(demoJpa.getLastName(),"Eden1");
    }

    @Test
    public void findByFirstNameIs() {
        DemoJpa demoJpa = repositories.findByFirstNameIs("May2");
        System.out.println(demoJpa);
        //Assert.assertNull(demoJpa);
    }

    @Test
    public void findByAgeBetween() {
        List<DemoJpa> demoJpaList = repositories.findByAgeBetween(15, 17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeLessThan() {
        List<DemoJpa> demoJpaList = repositories.findByAgeLessThan(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeLessThanEqual() {
        List<DemoJpa> demoJpaList = repositories.findByAgeLessThanEqual(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeGreaterThan() {
        List<DemoJpa> demoJpaList = repositories.findByAgeGreaterThan(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeGreaterThanEqual() {
        List<DemoJpa> demoJpaList = repositories.findByAgeGreaterThanEqual(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(3,demoJpaList.size());
    }

    @Test
    public void findByAgeAfter() {
        List<DemoJpa> demoJpaList = repositories.findByAgeAfter(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeBefore() {
        List<DemoJpa> demoJpaList = repositories.findByAgeBefore(17);
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByAgeIsNull() {
        List<DemoJpa> demoJpaList = repositories.findByAgeIsNull();
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByAgeNotNull() {
        List<DemoJpa> demoJpaList = repositories.findByAgeNotNull();
        System.out.println(demoJpaList);
        //Assert.assertEquals(5,demoJpaList.size());
    }

    @Test
    public void findByFirstNameLike() {
        DemoJpa demoJpa = repositories.findByFirstNameLike("May");
        System.out.println(demoJpa);
        //Assert.assertNotNull(demoJpa);
    }

    @Test
    public void findByFirstNameNotLike() {

    }

    @Test
    public void findByFirstNameStartingWith() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameStartingWith("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }

    @Test
    public void findByFirstNameEndingWith() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameEndingWith("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByFirstNameContaining() {
        List<DemoJpa> demoJpaList = repositories.findByFirstNameContaining("May");
        System.out.println(demoJpaList);
        //Assert.assertEquals(0,demoJpaList.size());
    }

    @Test
    public void findByAgeOrderByLastName() {
        List<DemoJpa> demoJpaList = repositories.findByAgeOrderByLastName(18);
        for (DemoJpa demoJpaL : demoJpaList){
            System.out.println("数据结果"+demoJpaL.toString());
            //log.info("数据结果"+demoJpaL.toString());
        }
    }

    @Test
    public void findByAgeNot() {
        List<DemoJpa> demoJpaList = repositories.findByAgeNot(20);
        System.out.println(demoJpaList);
        //Assert.assertEquals(5,demoJpaList.size());
    }

    @Test
    public void findByAgeIn() {
        List<DemoJpa> demoJpaList = repositories.findByAgeIn(Arrays.asList(15, 16));
        System.out.println(demoJpaList);
        //Assert.assertEquals(2,demoJpaList.size());
    }
}
