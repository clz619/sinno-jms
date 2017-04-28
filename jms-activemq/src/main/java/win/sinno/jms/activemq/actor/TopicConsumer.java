package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.ITopicConsumer;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * topic consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/18 17:52
 */
public class TopicConsumer implements ITopicConsumer {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActivemqClient activemqClient;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private String topicName;

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    private ConsumerHolderManager consumerHolderManager;

    private ConsumerHolder consumerHolder;

    private MessageListener messageListener;

    public TopicConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListener messageListener) {
        this(activemqClient, actorInfo, messageListener, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    public TopicConsumer(ActivemqClient activemqClient, ActorInfo actorInfo, MessageListener messageListener, boolean isTransacted, int sessionAckMode) {
        this.activemqClient = activemqClient;
        this.actorInfo = actorInfo;
        this.topicName = actorInfo.getNodename();
        this.connectionKey = new MqConnectionKey(actorInfo);
        this.messageListener = messageListener;
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;

        this.consumerHolderManager = new ConsumerHolderManager(actorInfo, connectionKey, isTransacted, sessionAckMode);
        this.consumerHolder = this.consumerHolderManager.conn();

        if (this.consumerHolder != null) {
            try {
                MessageConsumer consumer = consumerHolder.getConsumer();

                if (consumer != null) {
                    consumer.setMessageListener(messageListener);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public String getTopicName() {
        return topicName;
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {

        this.messageListener = messageListener;
        if (this.consumerHolder != null) {
            try {
                MessageConsumer consumer = consumerHolder.getConsumer();

                if (consumer != null) {
                    consumer.setMessageListener(messageListener);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public String consumer() {

        if (this.consumerHolder != null) {
            try {
                MessageConsumer consumer = this.consumerHolder.getConsumer();

                if (consumer != null) {
                    Message msg = consumer.receive();

                    TextMessage txtMsg = (TextMessage) msg;

                    return txtMsg.getText();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return null;
    }

    @Override
    public String consumer(Long timeout) {

        if (this.consumerHolder != null) {
            try {
                MessageConsumer consumer = this.consumerHolder.getConsumer();

                if (consumer != null) {
                    Message msg = consumer.receive(timeout);

                    TextMessage txtMsg = (TextMessage) msg;

                    return txtMsg.getText();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return null;
    }

    @Override
    public void close() {
        try {

            this.activemqClient.destroyConsumer(this);

            this.consumerHolderManager.close();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
