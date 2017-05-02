package win.sinno.jms.activemq.actor;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import win.sinno.jms.activemq.ActivemqClient;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.IQueueProducer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 16:14
 */
public class QueueProducer implements IQueueProducer, IActor {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActivemqClient client;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private boolean isReuse = true;

    private String queueName;

    private ProducerHolderManager producerHolderManager;

    private ProducerHolder producerHolder;

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    private ThreadLocal<ProducerHolder> threadProducerHolder = new ThreadLocal<ProducerHolder>();

    public QueueProducer(ActivemqClient activemqClient, ActorInfo actorInfo) {
        this(activemqClient, actorInfo, NodeConfigs.DEFAULT_IS_REUSE);
    }

    public QueueProducer(ActivemqClient client, ActorInfo actorInfo, Boolean isReuse) {
        this(client, actorInfo, isReuse, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    public QueueProducer(ActivemqClient client, ActorInfo actorInfo, Boolean isReuse, boolean isTransacted, int sessionAckMode) {
        this.client = client;
        this.actorInfo = actorInfo;
        this.connectionKey = new MqConnectionKey(actorInfo);
        this.queueName = actorInfo.getNodename();
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;

        producerHolderManager = new ProducerHolderManager(actorInfo, connectionKey, isTransacted, sessionAckMode);

        this.isReuse = isReuse;

        if (isReuse) {
            //复用
            producerHolder = producerHolderManager.conn();
        }
    }


    @Override
    public String getQueueName() {
        return queueName;
    }

    @Override
    public synchronized void close() {
        try {
            client.destroyProduce(this);

            producerHolderManager.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
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
                holder = producerHolderManager.conn();
            }
            threadProducerHolder.set(holder);

            TextMessage msg = holder.getSession().createTextMessage(message);
            holder.getProducer().send(msg);

            commit();

            return true;
        } catch (JMSException e) {
            String err = e.getMessage();
            try {
                rollback();
            } catch (JMSException e1) {
                LOG.error(e1.getMessage(), e1);
                err = err + " . " + e1.getMessage();
            }
            throw new JMSException(err);
        } finally {
            //不重用的producer 进行关闭
            threadProducerHolder.remove();
            if (!isReuse && holder != null) {
                producerHolderManager.close(holder);
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
        if (CollectionUtils.isEmpty(messages)) {
            return false;
        }

        ProducerHolder holder = null;
        try {
            if (isReuse) {
                holder = this.producerHolder;
            } else {
                holder = producerHolderManager.conn();
            }
            threadProducerHolder.set(holder);

            for (String message : messages) {
                TextMessage msg = holder.getSession().createTextMessage(message);
                holder.getProducer().send(msg);
            }

            commit();

            return true;
        } catch (Exception e) {
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
                producerHolderManager.close(holder);
            }
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
        return actorInfo;
    }

    @Override
    public void commit() throws JMSException {
        if (isTransacted()) {
            ProducerHolder holder = threadProducerHolder.get();
            if (holder == null) {
                return;
            }
            Session session = holder.getSession();
            if (session == null) {
                return;
            }
            // transfer TranscationContext
            session.commit();
        }
    }

    @Override
    public void rollback() throws JMSException {
        if (isTransacted()) {
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
}
