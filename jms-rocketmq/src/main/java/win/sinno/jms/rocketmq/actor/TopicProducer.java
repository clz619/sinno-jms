package win.sinno.jms.rocketmq.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import win.sinno.jms.api.IProducer;
import win.sinno.jms.rocketmq.IRocketmqClient;

/**
 * win.sinno.jms.rocketmq.actor.TopicProducer
 *
 * @author admin@chenlizhong.cn
 * @date 2018/7/2
 */
public class TopicProducer implements IRocketmqClient, IProducer {

  private DefaultMQProducer producer;

  private String namesrvAddr;

  private String group;

  private String topic;

  public static MessageQueueSelector DEFAULT_MESSAGE_QUEUE_SELECTOR = new MessageQueueSelector() {
    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
      Integer id = (Integer) arg;
      int index = id % mqs.size();
      return mqs.get(index);
    }
  };

  private MessageQueueSelector messageQueueSelector = DEFAULT_MESSAGE_QUEUE_SELECTOR;


  private AtomicBoolean isRunning = new AtomicBoolean(false);

  public TopicProducer(String namesrvAddr, String group, String topic) {
    this.namesrvAddr = namesrvAddr;
    this.group = group;
    this.topic = topic;

  }

  public TopicProducer(String namesrvAddr, String group, String topic,
      MessageQueueSelector messageQueueSelector) {
    this(namesrvAddr, group, topic);
    this.messageQueueSelector = messageQueueSelector;
  }


  public void start() throws MQClientException {

    if (isRunning.compareAndSet(false, true)) {
      producer = new DefaultMQProducer(group);
      producer.setNamesrvAddr(namesrvAddr);

      producer.start();
    }

  }

  @Override
  public boolean send(String message) throws Exception {

    Message msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));

    SendResult sendResult = producer.send(msg);

    return sendResult.getSendStatus() == SendStatus.SEND_OK;
  }

  /**
   * @param select 对应messageQueueSelector的arg，需要匹配它们的类型
   */
  public boolean send(String message, Object select)
      throws Exception {
    Message msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));

    SendResult sendResult = producer.send(msg, messageQueueSelector, select);

    return sendResult.getSendStatus() == SendStatus.SEND_OK;
  }

  @Override
  public boolean send(List<String> messages) throws Exception {

    if (messages == null || messages.size() == 0) {
      return false;
    }

    List<Message> msgs = new ArrayList<>();
    for (String message : messages) {
      Message msg = new Message(topic, message.getBytes(RemotingHelper.DEFAULT_CHARSET));
      msgs.add(msg);
    }

    SendResult sendResult = producer.send(msgs);

    return sendResult.getSendStatus() == SendStatus.SEND_OK;
  }

  @Override
  public boolean isTransacted() {
    return false;
  }

  @Override
  public int getSessionAckMode() {
    return 0;
  }

  @Override
  public void close() {
    if (isRunning.compareAndSet(true, false)) {
      producer.shutdown();
    }
  }

  @Override
  public void commit() throws Exception {

  }

  @Override
  public void rollback() throws Exception {

  }

  @Override
  public String getNamesrvAddr() {
    return namesrvAddr;
  }

  @Override
  public void setNamesrvAddr(String namesrvAddr) {
    this.namesrvAddr = namesrvAddr;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public void setGroup(String group) {
    this.group = group;
  }
}
