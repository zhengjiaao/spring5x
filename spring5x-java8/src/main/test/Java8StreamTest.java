import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Date: 2019-12-30 9:27
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：Java8 之 Stream流 测试
 */
public class Java8StreamTest extends BaseTest{

    /**
     * java8创建Stream流的几种方式
     */
    @Test
    public void creatStream(){

        //方式一：通过集合创建Stream流
        List<String> strings = Arrays.asList("宇宙小神特别萌", "宇宙小神特别萌1", "Hello", "HelloWorld", "宇宙小神特别萌2");
        //通过集合创建出一个Stream的方式也是比较常用的一种方式
        Stream<String> stream = strings.stream();
        //parallelStream可以为集合创建一个并行流。（多线程方式，需要考虑线程安全问题）
        //Stream<String> parallelStream = strings.parallelStream();

        //方式二：通过of创建Stream流
        //Stream<String> stream = Stream.of("宇宙小神特别萌", "宇宙小神特别萌1", "Hello", "HelloWorld", "宇宙小神特别萌2");

        //方式三：通过generate创建Stream流
        Stream<Double> generateA = Stream.generate(new Supplier<Double>() {
            @Override
            public Double get() {
                return java.lang.Math.random();
            }
        });
        Stream<Double> generateB = Stream.generate(()-> java.lang.Math.random());
        Stream<Double> generateC = Stream.generate(java.lang.Math::random);


        System.out.println(stream);
    }

    /**
     * 使用java8中的Stream流
     */
    @Test
    public void useStream(){

        List<String> strings = Arrays.asList("宇宙小神特别萌", "", "小神",  "小嗯嗯呢", "6666666666");

        //filter 过滤方法
        //stream流写法 ：filter 方法用于通过设置的条件过滤出元素（用于过滤得到一个新的流）
        strings.stream().filter(string -> !string.isEmpty()).forEach(System.out::println);
        //宇宙小神特别萌,"小神",  "小嗯嗯呢", "6666666666"
        //for写法
        /*for (String string : strings) {
            if (!string.isEmpty()) {
                System.out.println(string);
            }
        }*/

        //concat方法将两个Stream连接在一起，合成一个Stream
        Stream.concat(Stream.of(1, 2, 3), Stream.of(4, 5))
                .forEach(integer -> System.out.print("concat: "+integer + "  "));
        // 1  2  3  4  5

        System.out.println("map1: ");

        //map 方法用于映射每个元素到对应的结果
        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        numbers.stream().map( i -> i*i).forEach(System.out::println);
        //9,4,4,9,49,9,25

        System.out.println("map2: ");
        //map 转成Interger
        strings.stream().filter(string -> string.length() <= 6).map(String::length).sorted().distinct().forEach(System.out::println);
        //10,20,30

        System.out.println("flatMap: ");
        //flatMap方法与map方法类似，都是将原Stream中的每一个元素通过转换函数转换，不同的是，该换转函数的对象是一个Stream，也不会再创建一个新的Stream，而是将原Stream的元素取代为转换的Stream
        Stream.of(1, 2, 3)
                .flatMap(integer -> Stream.of(integer * 10))
                .forEach(System.out::println);
        // 10，20，30
        //传给flatMap中的表达式接受了一个Integer类型的参数，通过转换函数，将原元素乘以10后，生成一个只有该元素的流，该流取代原流中的元素。

        //peek方法生成一个包含原Stream的所有元素的新Stream，同时会提供一个消费函数（Consumer实例）
        Stream.of(1, 2, 3, 4, 5)
                .peek(integer -> System.out.println("accept:" + integer))
                .forEach(System.out::println);
        // accept:1
        //  1
        //  accept:2
        //  2
        //  accept:3
        //  3
        //  accept:4
        //  4
        //  accept:5
        //  5

        System.out.println("limit: ");
        //limit/skip： limit 返回 Stream 的前面 n 个元素；skip 则是扔掉前 n 个元素
        List<Integer> numbers2 = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        numbers2.stream().limit(4).forEach(System.out::println);
        //3,2,2,3

        System.out.println("sorted: ");
        //sorted 方法用于对流进行排序
        List<Integer> numbers3 = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        numbers3.stream().sorted().forEach(System.out::println);
        //2,2,3,3,3,5,7

        System.out.println("distinct: ");
        //distinct主要用来去重
        List<Integer> numbers4 = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        numbers4.stream().distinct().forEach(System.out::println);
        //3,2,7,5


        //例子：一个Stream先后通过filter、map、sort、limit处理后会发生什么
        List<String> strings2 = Arrays.asList("宇宙小神特别萌",  "小神", "小嗯嗯呢", "6666666666");
        //宇宙小神特别萌,大神 , 小菜鸡,交流群：549684836
        /*Stream<Integer> distinct = strings2.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct();*/
        System.out.println("strings2: ");
        strings2.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().forEach(System.out::println);
        //2,3


        System.out.println("forEach: ");
        //Stream 提供了方法 'forEach' 来迭代流中的每个数据
        Random random = new Random();
        random.ints().limit(10).forEach(System.out::println);

        System.out.println("count: ");
        //count用来统计流中的元素个数
        List<String> strings3 = Arrays.asList("宇宙小神特别萌", "小神", "小嗯嗯呢", "6666666666");
        long count = strings3.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().count();
        System.out.println(count);
        //2

        //collect就是一个归约操作，可以接受各种做法作为参数，将流中的元素累积成一个汇总结果
        List<String> strings4 = Arrays.asList("宇宙小神特别萌", "小神", "小嗯嗯呢", "6666666666");
        List<Integer> collect = strings4.stream().filter(string -> string.length() <= 6).map(String::length).sorted().limit(2)
                .distinct().collect(Collectors.toList());
        System.out.println("collect: "+collect);
        //最终得到一个List 数组，也就是流最终的归宿
    }

}
