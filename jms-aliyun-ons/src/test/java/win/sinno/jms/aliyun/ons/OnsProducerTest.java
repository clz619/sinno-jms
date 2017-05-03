package win.sinno.jms.aliyun.ons;

import com.aliyun.openservices.ons.api.*;
import org.junit.Test;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.jms.aliyun.ons.actor.TopicProducer;

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
 * ons producer test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/2 11:59
 */
public class OnsProducerTest {

    @Test
    public void testProducer() throws IOException {
        Properties properties = PropertiesUtil.loadFromResources("test.properties");

        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可
        producer.start();
//        int i = 0;
        //循环发送消息
        for (int i = 0; i < 10; i++) {
            Message msg = new Message( //
                    // Message所属的Topic
                    properties.getProperty(PropertyKeyConst.ProducerId),
                    // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                    "TagA",
                    // Message Body 可以是任何二进制形式的数据， MQ不做任何干预，
                    // 需要Producer与Consumer协商好一致的序列化和反序列化方式
                    ("Hello MQ" + i).getBytes());
            // 设置代表消息的业务关键属性，请尽可能全局唯一。
            // 以方便您在无法正常收到消息情况下，可通过阿里云服务器管理控制台查询消息并补发
            // 注意：不设置也不会影响消息正常收发
            msg.setKey("ORDERID_" + i);
            // 同步发送消息，只要不抛异常就是成功
            SendResult sendResult = producer.send(msg);

            System.out.println(sendResult);
        }
        // 在应用退出前，销毁Producer对象
        // 注意：如果不销毁也没有问题
        producer.shutdown();
    }

    @Test
    public void testP() throws Exception {
        Properties properties = PropertiesUtil.loadFromResources("test.properties");
        properties.put(win.sinno.jms.aliyun.ons.configs.PropertyKeyConst.tag, "tag1");
        TopicProducer topicProducer = new TopicProducer(properties);
        topicProducer.send("haha");

    }

}
