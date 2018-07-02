package win.sinno.jms.rocketmq;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * win.sinno.jms.rocketmq.BroadcastConsumer
 *
 * @author admin@chenlizhong.cn
 * @date 2018/3/21
 */
public class BroadcastConsumer {

  public static void main(String[] args) throws Exception {
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("sinno");
    consumer.setNamesrvAddr("127.0.0.1:9876");

    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

    //set to broadcast mode
    consumer.setMessageModel(MessageModel.CLUSTERING);

    consumer.subscribe("test", "*");

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

    consumer.registerMessageListener(messageListenerOrderly);

//    consumer.registerMessageListener(new MessageListenerConcurrently() {
//
//      @Override
//      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//          ConsumeConcurrentlyContext context) {
//
////        System.out
////            .printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
//
//        for (MessageExt msg : msgs) {
//
//          try {
//            String message = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
//            System.out.println(message);
//          } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//          }
//
//        }
//
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//      }
//    });

    consumer.start();
    System.out.printf("Broadcast Consumer Started.%n");
  }
}
