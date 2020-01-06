import com.zja.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * Date: 2019-12-30 10:40
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：java8 防止空指针异常的Optional类
 */
public class Java8OptionalTest extends BaseTest{

    /**
     * 创建Optional容器有两种方式:
     * 1.调用ofNullable()方法，传入的对象可以为null
     * 2.调用of()方法，传入的对象不可以为null，否则抛出NullPointerException
     */
    @Test
    public void creatOptional(){

        UserEntity userEntity = new UserEntity();
        UserEntity userEntity2 = null;

        //1.调用ofNullable()方法，传入的对象可以为null
        Optional<UserEntity> entity1 = Optional.ofNullable(userEntity);
        Optional<UserEntity> entity2 = Optional.ofNullable(userEntity2);  //不报错，所以一般用这个

        //2.调用of()方法，传入的对象不可以为null，否则抛出NullPointerException
        Optional<UserEntity> of1 = Optional.of(userEntity);
        Optional<UserEntity> of2 = Optional.of(userEntity2); //报异常
    }

    /**
     * 使用Optinal方法
     */
    @Test
    public void useOptinal(){
        UserEntity userEntity = new UserEntity();
        userEntity.setName("宇宙小神特别萌");
        userEntity.setAge(18);

        UserEntity userEntity1=null;

        //1.调用ofNullable()方法，传入的对象可以为null
        Optional<UserEntity> entity1 = Optional.ofNullable(userEntity);
        Optional<UserEntity> entity2 = Optional.ofNullable(userEntity1);  //不报错，所以一般用这个

        //如果容器中的对象存在，则返回。否则返回传递进来的参数。但是orElse方法的other参数泛型要和前面ofNullable的泛型相同
        System.out.println(entity1.orElse(null));
        System.out.println(entity2.orElse(null));

    }

    /**
     * Optinal高级用法：使用函数式编程，简化代码
     */
    @Test
    public void OptinalSenior(){
        UserEntity userEntity = new UserEntity();
        userEntity.setName("宇宙小神特别萌");
        userEntity.setAge(18);

        Optional<UserEntity> optional = Optional.ofNullable(userEntity);

        //ifPresent方法：如果对象不为null，则获取对象属性
        optional.ifPresent((s->System.out.println(s.getName())));//宇宙小神特别萌
        System.out.println("***********");
        //旧写法
        /*if (null!=userEntity){
            System.out.println(userEntity.getName());//宇宙小神特别萌
        }*/

        //orElseGet和orElseThrow方法：
        // orElseGet如果对象存在，则直接返回，否则返回由Supplier接口的实现用来生成默认值
        // orElseThrow如果存在，则返回。否则抛出supplier接口创建的异常

        // orElseGet方法：如果存在user，则直接返回，否则创建出一个新的User对象
        UserEntity entity = optional.orElseGet(() -> new UserEntity());
        System.out.println(entity);
        // 旧写法
        /*if (userEntity != null) {
            userEntity = new UserEntity();
        }
        System.out.println(userEntity);*/

        //filter方法:如果容器中的对象存在，并且符合过滤条件，返回装载对象的Optional容器，否则返回一个空的Optional容器
        UserEntity entity1 = optional.filter((value) -> "宇宙小神特别萌".equals(value.getName())).orElse(null);
        System.out.println("entity1: " +entity1.getName());


        //map方法:如果容器的对象存在，则对其执行调用mapping函数得到返回值。然后创建包含mapping返回值的Optional，否则返回空Optional。
        optional.map(entity2 -> entity2.getName()).orElse("宇宙小神特别萌");
        ///旧写法
        /*if (userEntity != null) {
            return userEntity.getName();
        }else{
            return "宇宙小神特别萌";
        }*/

        //flatMap方法与map方法类似，区别在于apply函数的返回值不同。map方法的apply函数返回值是? extends U，而flatMap方法的apply函数返回值必须是Optional


    }

}
