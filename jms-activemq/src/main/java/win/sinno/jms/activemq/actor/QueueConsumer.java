package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.IQueueConsumer;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

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

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    private ConsumerHolderManager consumerHolderManager;

    private ConsumerHolder consumerHolder;

    public QueueConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListener messageListener) {
        this(activemqClient, actorInfo, messageListener, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    public QueueConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListener messageListener, boolean isTransacted, int sessionAckMode) {
        this.activemqClient = activemqClient;
        this.actorInfo = actorInfo;
        this.queueName = actorInfo.getNodename();
        this.connectionKey = new MqConnectionKey(actorInfo);
        this.messageListener = messageListener;
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;

        consumerHolderManager = new ConsumerHolderManager(actorInfo, connectionKey, isTransacted, sessionAckMode);

        consumerHolder = consumerHolderManager.conn();

        if (consumerHolder != null) {
            MessageConsumer consumer = consumerHolder.getConsumer();

            try {
                consumer.setMessageListener(messageListener);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;

        if (consumerHolder != null) {
            MessageConsumer consumer = consumerHolder.getConsumer();

            try {
                consumer.setMessageListener(messageListener);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

        }
    }

    @Override
    public String consumer() {

        try {
            MessageConsumer consumer = consumerHolder.getConsumer();

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
            MessageConsumer consumer = consumerHolder.getConsumer();

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
