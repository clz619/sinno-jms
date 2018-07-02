package win.sinno.jms.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * win.sinno.jms.rocketmq.OnewayProducer
 *
 * @author admin@chenlizhong.cn
 * @date 2018/3/21
 */
public class OnewayProducer {

  public static void main(String[] args) throws Exception {
    //Instantiate with a producer group name.
    DefaultMQProducer producer = new DefaultMQProducer("sinno");
    producer.setNamesrvAddr("127.0.0.1:9876");

    //Launch the instance.
    producer.start();

    for (int i = 0; i < 100; i++) {

      //Create a message instance, specifying topic, tag and message body.
      Message msg = new Message("test" /* Topic */,
          "c" /* Tag */,
          ("Hello RocketMQ " +
              i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
      );

      //Call send message to deliver message to one of brokers.
      producer.sendOneway(msg);

      System.out.println("send one way:" + i);
    }

    //Shut down once the producer instance is not longer in use.
    producer.shutdown();
  }
}
