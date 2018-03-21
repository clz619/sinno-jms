package win.sinno.jms.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * win.sinno.jms.rocketmq.SyncProducer
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/3/21
 */
public class SyncProducer {

  public static void main(String[] args) throws Exception {
    DefaultMQProducer producer = new DefaultMQProducer("sinno");

    producer.setNamesrvAddr("127.0.0.1:9876");

    producer.start();

    for (int i = 0; i < 100; i++) {

      Message msg = new Message("test", "A",
          ("hello rocketmq" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));

      SendResult sendResult = producer.send(msg);

      System.out.println("send:" + i + " send result:" + sendResult);
    }

    producer.shutdown();

  }

}
