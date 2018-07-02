package win.sinno.jms.rocketmq.actor;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.jms.api.IConsumer;
import win.sinno.jms.api.MessageListenerHolder;
import win.sinno.jms.rocketmq.IRocketmqClient;

/**
 * win.sinno.jms.rocketmq.actor.TopicConsumer
 *
 * @author admin@chenlizhong.cn
 * @date 2018/7/2
 */
public class TopicConsumer implements IConsumer, IRocketmqClient {

  private static final Logger LOG = LoggerFactory.getLogger(TopicConsumer.class);

  private DefaultMQPushConsumer consumer;

  private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;

  private MessageModel messageModel = MessageModel.CLUSTERING;

  private String namesrvAddr;

  private String group;

  private String topic;

  private String subExpression;

  private MessageListenerHolder messageListenerHolder;

  private AtomicBoolean isRunning = new AtomicBoolean(false);

  public TopicConsumer(String namesrvAddr, String group, String topic, String subExpression,
      MessageListenerHolder messageListenerHolder) throws MQClientException {
    this.namesrvAddr = namesrvAddr;
    this.group = group;
    this.topic = topic;
    this.subExpression = subExpression;
    this.messageListenerHolder = messageListenerHolder;

  }

  public TopicConsumer(String namesrvAddr, String group, String topic, String subExpression,
      MessageListenerHolder messageListenerHolder, ConsumeFromWhere consumeFromWhere,
      MessageModel messageModel) throws MQClientException {
    this.namesrvAddr = namesrvAddr;
    this.group = group;
    this.topic = topic;
    this.subExpression = subExpression;
    this.messageListenerHolder = messageListenerHolder;
    this.consumeFromWhere = consumeFromWhere;
    this.messageModel = messageModel;


  }

  public void start() throws MQClientException {

    if (isRunning.compareAndSet(false, true)) {
      consumer = new DefaultMQPushConsumer(group);
      consumer.setNamesrvAddr(namesrvAddr);
      consumer.setConsumeFromWhere(consumeFromWhere);
      consumer.setMessageModel(messageModel);

      consumer.subscribe(topic, subExpression);

      setMessageListener(messageListenerHolder);

      consumer.start();
    }

  }

  @Override
  public void setMessageListener(MessageListenerHolder messageListenerHolder) {
    if (messageListenerHolder == null) {
      return;
    }

    Object holder = messageListenerHolder.get();
    MessageListener messageListener = null;
    if (holder != null && holder instanceof MessageListener) {
      messageListener = (MessageListener) holder;
    }
    if (consumer != null && messageListener != null) {
      consumer.registerMessageListener(messageListener);
    }
  }

  @Override
  public String consumer() {
    return null;
  }

  @Override
  public String consumer(Long timeout) {
    return null;
  }

  @Override
  public void close() {
    if (isRunning.compareAndSet(true, false)) {
      consumer.shutdown();
    }
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public void setGroup(String group) {
    this.group = group;
  }

  @Override
  public String getNamesrvAddr() {
    return namesrvAddr;
  }

  @Override
  public void setNamesrvAddr(String namesrvAddr) {
    this.namesrvAddr = namesrvAddr;
  }
}
