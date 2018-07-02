package win.sinno.jms.rocketmq;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.jupiter.api.Test;
import win.sinno.jms.api.MessageListenerHolder;
import win.sinno.jms.rocketmq.actor.TopicConsumer;

/**
 * win.sinno.jms.rocketmq.TopicConsumerTest
 *
 * @author admin@chenlizhong.cn
 * @date 2018/7/2
 */
public class TopicConsumerTest {

  public static void main(String[] args) throws MQClientException {
    TopicConsumerTest test = new TopicConsumerTest();
    test.consume();
  }

  @Test
  public void consume() throws MQClientException {
    String namesrvAddr = "127.0.0.1:9876";
    String group = "sinno";
    String topic = "test";

    MessageListenerOrderly messageListenerOrderly = new MessageListenerOrderly() {
      @Override
      public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
          ConsumeOrderlyContext context) {

        for (MessageExt msg : msgs) {

          try {
            String message = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
            System.out.println(message);
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }

        }

        return ConsumeOrderlyStatus.SUCCESS;
      }
    };

    MessageListenerHolder messageListenerHolder = new MessageListenerHolder(messageListenerOrderly);

    TopicConsumer consumer = new TopicConsumer(namesrvAddr, group, topic, "*",
        messageListenerHolder);

    consumer.start();

  }
}
