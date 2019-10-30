import com.zja.util.EmailUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhengJa
 * @description 邮件发送单元测试
 * @data 2019/10/30
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:META-INF/spring/spring-mvc.xml")
public class SendEmail {

    @Autowired
    private EmailUtil emailUtil;

    // 发送方邮箱地址
    private static final String from = "1263598336@qq.com";
    // 发送方邮箱地址对应的授权码
    private static final String authWord = "yoypgcyowgofieda";
    // 接收方邮箱地址
    private static final String to = "953649948@qq.com";


    /**
     * 简单邮件测试
     */
    @Test
    public void sendMessage() {
        emailUtil.sendTextMessage(from, authWord, to, "spring 简单邮件", "Hello Spring Email!");
    }

    /**
     * 发送带附件的邮件
     */
    @Test
    public void sendComplexMessage() {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("image1.jpg", new File("D:\\FileTest\\图\\cube01.bmp"));
        fileMap.put("image2.jpg", new File("D:\\FileTest\\图\\头像.jpg"));
        emailUtil.sendEmailWithAttachments(from, authWord, to, "spring 多附件邮件"
                , "Hello Spring Email!", fileMap);
    }

    /**
     * 发送内嵌资源的邮件
     */
    @Test
    public void sendEmailWithInline() {
        emailUtil.sendEmailWithInline(from, authWord, to, "spring 内嵌资源邮件"
                , "Hello Spring Email!", new File("D:\\FileTest\\图\\头像.jpg"));
    }

    /**
     * 发送模板邮件
     */
    @Test
    public void sendEmailByTemplate() {
        emailUtil.sendEmailByTemplate(from, authWord, to,
                "spring 模板邮件", "Hello Spring Email!");
    }

}
