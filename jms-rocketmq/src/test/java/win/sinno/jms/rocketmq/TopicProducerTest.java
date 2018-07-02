package win.sinno.jms.rocketmq;

import java.util.Date;
import java.util.List;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.jupiter.api.Test;
import win.sinno.jms.rocketmq.actor.TopicProducer;

/**
 * win.sinno.jms.rocketmq.TopicProducerTest
 *
 * @author admin@chenlizhong.cn
 * @date 2018/7/2
 */
public class TopicProducerTest {


  @Test
  public void test() throws Exception {

    String namesrvAddr = "127.0.0.1:9876";
    String group = "sinno";
    String topic = "test";

    TopicProducer producer = new TopicProducer(namesrvAddr, group, topic);
    producer.send("hello " + new Date());
  }

  @Test
  public void testOrder() throws Exception {
    String namesrvAddr = "127.0.0.1:9876";
    String group = "sinno";
    String topic = "test";

    MessageQueueSelector selector = new MessageQueueSelector() {
      @Override
      public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        Integer id = (Integer) arg;
        int index = id % mqs.size();
        return mqs.get(index);
      }
    };

    TopicProducer producer = new TopicProducer(namesrvAddr, group, topic, selector);
    producer.start();

    producer.send("hello " + new Date(), 1);
  }
}
