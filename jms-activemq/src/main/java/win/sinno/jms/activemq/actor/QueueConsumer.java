package win.sinno.jms.activemq.actor;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.IQueueConsumer;
import win.sinno.jms.api.MessageListenerHolder;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * queue consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 16:32
 */
public class QueueConsumer implements IQueueConsumer {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActivemqClient activemqClient;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private String queueName;

    private MessageListener messageListener;

    private ConsumerHolderManager consumerHolderManager;

    private List<ConsumerHolder> consumerHolderList = new ArrayList<>();

    private ConsumerHolder lastConsumerHolder;

    private int concurrentNum;

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    public QueueConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListenerHolder messageListenerHolder, int concurrentNum) {
        this(activemqClient, actorInfo, messageListenerHolder, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE, concurrentNum);
    }

    public QueueConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListenerHolder messageListenerHolder, boolean isTransacted, int sessionAckMode, int concurrentNum) {
        this.activemqClient = activemqClient;
        this.actorInfo = actorInfo;
        this.queueName = actorInfo.getNodename();
        this.connectionKey = new MqConnectionKey(actorInfo);

        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;

        if (concurrentNum <= 0) {
            concurrentNum = 1;
        }
        this.concurrentNum = concurrentNum;

        consumerHolderManager = new ConsumerHolderManager(actorInfo, connectionKey, isTransacted, sessionAckMode);

        for (int i = 0; i < concurrentNum; i++) {
            lastConsumerHolder = consumerHolderManager.conn();

            consumerHolderList.add(lastConsumerHolder);
        }

        setMessageListener(messageListenerHolder);

    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    public void setMessageListener(MessageListenerHolder messageListenerHolder) {
        if (messageListenerHolder == null) {
            return;
        }

        Object holder = messageListenerHolder.get();

        if (holder != null && holder instanceof MessageListener) {
            this.messageListener = (MessageListener) holder;
        } else {
            return;
        }

        if (CollectionUtils.isNotEmpty(consumerHolderList) && messageListener != null) {

            Iterator<ConsumerHolder> it = consumerHolderList.iterator();

            while (it.hasNext()) {

                ConsumerHolder consumerHolder = it.next();

                if (consumerHolder != null) {
                    MessageConsumer consumer = consumerHolder.getConsumer();

                    setMessageListener(consumer, messageListener);
                }
            }
        }

    }

    private void setMessageListener(MessageConsumer messageConsumer, MessageListener messageListener) {
        try {
            if (messageConsumer != null && messageListener != null) {
                messageConsumer.setMessageListener(messageListener);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String consumer() {

        try {
            MessageConsumer consumer = lastConsumerHolder.getConsumer();

            if (consumer != null) {
                Message msg = consumer.receive();

                TextMessage txtMsg = (TextMessage) msg;

                txtMsg.acknowledge();

                return txtMsg.getText();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return null;

    }

    @Override
    public String consumer(Long timeout) {

        try {
            MessageConsumer consumer = lastConsumerHolder.getConsumer();

            if (consumer != null) {
                Message msg = consumer.receive(timeout);

                if (msg != null) {

                    TextMessage txtMsg = (TextMessage) msg;

                    return txtMsg.getText();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void close() {

        try {
            activemqClient.destroyConsumer(this);

            consumerHolderManager.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
