package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.ITopicProducer;

import javax.jms.TextMessage;
import java.util.List;

/**
 * topic producer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/18 17:55
 */
public class TopicProducer implements ITopicProducer, IActor {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActivemqClient activemqClient;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private String topicName;

    private ProducerHolderManager producerHolderManager;

    private ProducerHolder producerHolder;

    private boolean isReuse = false;

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    public TopicProducer(ActivemqClient activemqClient, ActorInfo actorInfo) {
        this(activemqClient, actorInfo, NodeConfigs.DEFAULT_IS_REUSE);
    }

    public TopicProducer(ActivemqClient activemqClient, ActorInfo actorInfo, boolean isReuse) {
        this(activemqClient, actorInfo, isReuse, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    public TopicProducer(ActivemqClient activemqClient, ActorInfo actorInfo, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        this.activemqClient = activemqClient;
        this.actorInfo = actorInfo;
        this.connectionKey = new MqConnectionKey(actorInfo);
        this.topicName = actorInfo.getNodename();
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;

        this.producerHolderManager = new ProducerHolderManager(actorInfo, connectionKey, isTransacted, sessionAckMode);

        this.isReuse = isReuse;
        if (isReuse) {
            this.producerHolder = producerHolderManager.conn();
        }
    }


    @Override
    public String getTopicName() {
        return topicName;
    }

    /**
     * send
     *
     * @param message
     */
    @Override
    public boolean send(String message) {
        ProducerHolder holder = null;

        try {
            if (isReuse) {
                holder = this.producerHolder;
            } else {
                holder = this.producerHolderManager.conn();
            }

            TextMessage msg = this.producerHolder.getSession().createTextMessage(message);
            this.producerHolder.getProducer().send(msg);

            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (!isReuse && holder != null) {
                this.producerHolderManager.close(holder);
            }
        }
        return false;
    }

    /**
     * send messages(List)
     *
     * @param messages
     */
    @Override
    public boolean send(List<String> messages) {
        // TODO
        return false;
    }

    @Override
    public void close() {
        try {
            this.activemqClient.destroyProduce(this);

            this.producerHolderManager.close();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void commit() {
        // TODO
    }

    @Override
    public void rollback() {
        // TODO
    }

    /**
     * session是否开启事务
     *
     * @return
     */
    @Override
    public boolean isTransacted() {
        return this.isTransacted;
    }

    /**
     * session ack mode
     *
     * @return
     */
    @Override
    public int getSessionAckMode() {
        return this.sessionAckMode;
    }

    /**
     * @return
     */
    @Override
    public ActorInfo getActorInfo() {
        return this.actorInfo;
    }
}
