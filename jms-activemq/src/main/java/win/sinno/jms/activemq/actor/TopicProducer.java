package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.ITopicProducer;

import javax.jms.JMSException;
import javax.jms.Session;
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

    private ThreadLocal<ProducerHolder> threadProducerHolder = new ThreadLocal<ProducerHolder>();

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
    public boolean send(String message) throws JMSException {
        ProducerHolder holder = null;
        try {
            if (isReuse) {
                holder = this.producerHolder;
            } else {
                holder = this.producerHolderManager.conn();
            }
            threadProducerHolder.set(holder);

            TextMessage msg = this.producerHolder.getSession().createTextMessage(message);
            this.producerHolder.getProducer().send(msg);

            commit();
            return true;
        } catch (JMSException e) {
            String err = e.getMessage();
            try {
                rollback();
            } catch (JMSException e1) {
                err = err + " . " + e1.getMessage();
            }
            throw new JMSException(err);
        } finally {
            threadProducerHolder.remove();
            if (!isReuse && holder != null) {
                this.producerHolderManager.close(holder);
            }
        }
    }

    /**
     * send messages(List)
     *
     * @param messages
     */
    @Override
    public boolean send(List<String> messages) throws JMSException {
        ProducerHolder holder = null;
        try {
            if (isReuse) {
                holder = this.producerHolder;
            } else {
                holder = this.producerHolderManager.conn();
            }
            threadProducerHolder.set(holder);

            for (String message : messages) {
                TextMessage msg = this.producerHolder.getSession().createTextMessage(message);
                this.producerHolder.getProducer().send(msg);
            }

            commit();
            return true;
        } catch (JMSException e) {
            String err = e.getMessage();
            try {
                rollback();
            } catch (JMSException e1) {
                err = err + " . " + e1.getMessage();
            }
            throw new JMSException(err);
        } finally {
            threadProducerHolder.remove();
            if (!isReuse && holder != null) {
                this.producerHolderManager.close(holder);
            }
        }
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
    public void commit() throws JMSException {

        if (isTransacted) {
            ProducerHolder holder = threadProducerHolder.get();

            if (holder == null) {
                return;
            }

            Session session = holder.getSession();
            if (session == null) {
                return;
            }

            session.commit();
        }

    }

    @Override
    public void rollback() throws JMSException {
        if (isTransacted) {
            ProducerHolder holder = threadProducerHolder.get();

            if (holder == null) {
                return;
            }

            Session session = holder.getSession();
            if (session == null) {
                return;
            }

            session.rollback();
        }

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
