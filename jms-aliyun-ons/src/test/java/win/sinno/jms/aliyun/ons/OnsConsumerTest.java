package win.sinno.jms.aliyun.ons;

import com.aliyun.openservices.ons.api.*;
import org.junit.Test;
import win.sinno.common.util.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * ━━━━━━oooo━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━oooo━━━━━━
 * <p>
 * ons consumer test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/2 13:32
 */
public class OnsConsumerTest {

    @Test
    public void testConsumer() throws InterruptedException, IOException {


        Properties properties = PropertiesUtil.loadFromResources("test.properties");

        properties.put(PropertyKeyConst.ConsumerId, "CID_test2324_1");// 您在控制台创建的 Consumer ID

        Consumer consumer = ONSFactory.createConsumer(properties);

        consumer.subscribe("tes2324", "*", new MessageListener() { //订阅多个Tag
            public Action consume(Message message, ConsumeContext context) {
                System.out.println("Receive: " + message);
                byte[] bodyBytes = message.getBody();
                String msg = new String(bodyBytes);
                System.out.println("Receive msg: " + msg);
                return Action.CommitMessage;
            }
        });

        consumer.start();

        System.out.println("Consumer Started");

        Thread.sleep(10000000l);
    }
}
