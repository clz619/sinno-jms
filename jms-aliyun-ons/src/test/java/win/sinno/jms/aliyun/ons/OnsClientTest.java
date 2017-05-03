package win.sinno.jms.aliyun.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.junit.Test;
import win.sinno.common.util.PropertiesUtil;
import win.sinno.jms.api.IConsumer;
import win.sinno.jms.api.IProducer;
import win.sinno.jms.api.MessageListenerHolder;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * ons client test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 10:42
 */
public class OnsClientTest {

    @Test
    public void testOnsClient() throws Exception {

        Properties properties = PropertiesUtil.loadFromResources("test.properties");

        OnsClient onsClient = OnsClientFactory.builder()
                .name("test")
                .properties(properties)
                .build();

        IProducer producer = onsClient.createTopicProducer();

        MessageListener messageListener = new MessageListener() {
            @Override
            public Action consume(Message message, ConsumeContext consumeContext) {
                byte[] bytes = message.getBody();
                try {
                    String msg = new String(bytes, "UTF-8");
                    System.out.println("rcv:(" + msg + ")");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return Action.CommitMessage;
            }
        };

        MessageListenerHolder messageListenerHolder = new MessageListenerHolder(messageListener);

        IConsumer consumer = onsClient.createTopicConsumer(messageListenerHolder);

        for (int i = 0; i < 20; i++) {
            String m = i + " . time:" + new Date();
            producer.send(m);
            System.out.println("send:(" + m + ")");
        }

        Thread.sleep(1000000l);
    }
}
