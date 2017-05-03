package win.sinno.jms.aliyun.ons;

import com.aliyun.openservices.ons.api.*;
import org.junit.Test;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.jms.aliyun.ons.actor.TopicConsumer;
import win.sinno.jms.api.MessageListenerHolder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

        Consumer consumer = ONSFactory.createConsumer(properties);

        consumer.subscribe(properties.getProperty(PropertyKeyConst.ConsumerId), "*", new MessageListener() { //订阅多个Tag
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

    @Test
    public void testC() throws Exception {
        Properties properties = PropertiesUtil.loadFromResources("test.properties");
        properties.put(win.sinno.jms.aliyun.ons.configs.PropertyKeyConst.tag, "*");
        MessageListener messageListener = new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext consumeContext) {
                byte[] bytes = message.getBody();
                try {
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("rcv msg:" + msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Action.CommitMessage;
            }
        };

        MessageListenerHolder messageListenerHolder = new MessageListenerHolder(messageListener);

        TopicConsumer consumer = new TopicConsumer(properties, messageListenerHolder);

        System.out.println("Consumer Started");

        Thread.sleep(10000000l);
    }
}
