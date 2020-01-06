import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Date: 2019-12-30 13:30
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：java8 之 Lambda 表达式，也可称为闭包，允许把函数作为一个参数，使代码更简洁
 */
public class Java8LambdaTest extends BaseTest {

    /**
     * Lambda 表达式基本语法：
     * <p>
     * // 1. 不需要参数,返回值为 5
     * () -> 5
     * <p>
     * // 2. 接收一个参数(数字类型),返回其2倍的值
     * x -> 2 * x
     * <p>
     * // 3. 接受2个参数(数字),并返回他们的差值
     * (x, y) -> x – y
     * <p>
     * // 4. 接收2个int型整数,返回他们的和
     * (int x, int y) -> x + y
     * <p>
     * // 5. 接受一个 string 对象,并在控制台打印,不返回任何值(看起来像是返回void)
     * (String s) -> System.out.print(s)
     */


    //创建线程
    @Test
    public void creatThread() {

        //用匿名类方式创建线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Runnable 宇宙小神特别萌");
            }
        }).start();

        //用Lambda来创建线程
        new Thread(() -> System.out.println("Lambda 宇宙小神特别萌")).start();
    }

    //遍历Map集合
    @Test
    public void traverseMap() {

        Map<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("宇宙小神特别萌", "宇宙小神特别萌");

        //增强for方式比遍历hashMap
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("lambda: ");
        //Lambda表达式来遍历hashMap
        map.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    //函数式接口-必须先声明接口，很麻烦
    @Test
    public void function() {
        //Lambda的写法
        System.out.println(toUpperString(str -> str.toUpperCase(), "abc")); //ABC
        //匿名内部类的写法
        System.out.println(toUpperString(new MyNumber<String>() {
            @Override
            public String getValue(String s) {
                return s;
            }
        }, "abc"));

    }

    static String toUpperString(MyNumber<String> mn, String str) {
        return mn.getValue(str);
    }

    @FunctionalInterface
    interface MyNumber<T> {
        T getValue(T t);
    }


    //Java 内置四大核心函数式接口
    @Test
    public void functionalInterface() {
        //Lambda的写法
        System.out.println(toUpperString2(str -> str.toUpperCase(), "abc")); //ABC
        //匿名内部类的写法
        System.out.println(toUpperString2(new MyNumber<String>() {
            @Override
            public String getValue(String s) {
                return s;
            }
        }, "abc"));

        //使用内置的函数式接口的lambda写法
        System.out.println(toUpperString1(str -> str.toUpperCase(), "abc"));
    }
    //内置的函数接口
    public static String toUpperString1(Function<String, String> mn, String str) {
        return mn.apply(str);
    }
    //定义的函数接口
    public static String toUpperString2(MyNumber<String> mn, String str) {
        return mn.getValue(str);
    }

    //方法引用与构造器引用
    //方法引用：当要传递给Lambda体的操作，已经有实现的方法了，可以使用方法引用！ （实现抽象方法的参数列表，必须与方法引用方法的参数列表保持一致！）
    //方法引用：使用操作符 “::” 将方法名和对象或类的名字分隔开来
    //对象::实例方法
    //类::静态方法
    //类::实例方法
    @Test
    public void test(){
        PrintStream printStream=System.out;
        Consumer<String> con= printStream::println;
        con.accept("haha");
    }
    //类::静态方法名
    Comparator<Integer> com=Integer::compare;
    Comparator<Integer> com1=(x,y)->Integer.compare(x,y);

    //例如：此时Consumer参数类型和返回值和println方法一致
    //对象::实例方法名
    //为什么这样用，因为 ConSumer这个函数接口就是传入参数，返回为void ,并且printl 方法就是这样的
    /**我们看下面的源码 和那个函数接口一样
     *     public void println(String x) {
     *         synchronized (this) {
     *             print(x);
     *             newLine();
     *         }
     *     }
*/

}
