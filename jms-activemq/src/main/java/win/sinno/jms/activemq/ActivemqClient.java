package win.sinno.jms.activemq;

import org.slf4j.Logger;
import win.sinno.jms.activemq.actor.*;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.api.*;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * activemq client
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 15:17
 */
public class ActivemqClient implements IClient {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private String name;

    private String brokerURL;

    private String username;

    private String password;

    private Properties properties;

    private IClientConfig clientConfig;

    // consumers
    private CopyOnWriteArrayList<IConsumer> consumers = new CopyOnWriteArrayList<IConsumer>();

    // producers
    private CopyOnWriteArrayList<IProducer> producers = new CopyOnWriteArrayList<IProducer>();

    private Object consumerLock = new Object();

    private Object producerLock = new Object();

    ActivemqClient() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getBrokerURL() {
        return brokerURL;
    }

    @Override
    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public IClientConfig getClientConfig() {
        return clientConfig;
    }

    @Override
    public void setClientConfig(IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /**
     * create producer
     * type=0 queue
     * type=1 topic
     * <p>
     * default reuse conn and session
     *
     * @param name
     * @param type
     * @return
     */
    @Override
    public IProducer createProducer(String name, Integer type) {
        return createProducer(name, type, NodeConfigs.DEFAULT_IS_REUSE);
    }

    /**
     * @param name node name
     * @param type {@link NodeType}
     * @return
     */
    @Override
    public IProducer createProducer(String name, Integer type, boolean isReuse) {
        return createProducer(name, type, isReuse, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    @Override
    public IProducer createProducer(String name, Integer type, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        IProducer producer = null;
        if (NodeType.QUEUE.getCode() == type) {
            producer = createQueueProducer(name, isReuse, isTransacted, sessionAckMode);
        } else {
            producer = createTopicProducer(name, isReuse, isTransacted, sessionAckMode);
        }
        return producer;
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName) {
        return createQueueProducer(queueName, NodeConfigs.DEFAULT_IS_REUSE);
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName, boolean isReuse) {
        return createQueueProducer(queueName, isReuse, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        IQueueProducer p = null;

        try {
            ActorInfo actorInfo = ActorInfoFactory.builder()
                    .clientName(name)
                    .brokerURL(brokerURL)
                    .username(username)
                    .password(password)
                    .nodename(queueName)
                    .nodeType(NodeType.QUEUE.getCode())
                    .actorType(ActorType.PRODUCER.getCode())
                    .build();

            p = new QueueProducer(this, actorInfo, isReuse, isTransacted, sessionAckMode);

            addProduce(p);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return p;
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName) {
        return createTopicProducer(topicName, NodeConfigs.DEFAULT_IS_REUSE);
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName, boolean isReuse) {
        return createTopicProducer(topicName, isReuse, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        ITopicProducer p = null;

        try {
            ActorInfo actorInfo = ActorInfoFactory.builder()
                    .clientName(name)
                    .brokerURL(brokerURL)
                    .username(username)
                    .password(password)
                    .nodename(topicName)
                    .nodeType(NodeType.TOPIC.getCode())
                    .actorType(ActorType.PRODUCER.getCode())
                    .build();

            p = new TopicProducer(this, actorInfo, isReuse, isTransacted, sessionAckMode);

            addProduce(p);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return p;
    }

    /**
     * create consumer
     *
     * @param name
     * @param type
     * @return
     */
    @Override
    public IConsumer createConsumer(String name, Integer type) {
        IConsumer consumer = null;

        if (NodeType.QUEUE.getCode() == type) {
            consumer = createQueueConsumer(name);
        } else {
            consumer = createTopicConsumer(name);
        }

        return consumer;
    }

    @Override
    public IQueueConsumer createQueueConsumer(String queueName) {

        return createQueueConsumer(queueName, null);
    }

    @Override
    public IQueueConsumer createQueueConsumer(String queueName, MessageListenerHolder messageListenerHolder) {
        return createQueueConsumer(queueName, null, 1);
    }

    @Override
    public IQueueConsumer createQueueConsumer(String queueName, MessageListenerHolder messageListenerHolder, int concurrentNum) {
        IQueueConsumer c = null;

        try {
            ActorInfo actorInfo = ActorInfoFactory.builder()
                    .clientName(name)
                    .brokerURL(brokerURL)
                    .username(username)
                    .password(password)
                    .nodename(queueName)
                    .nodeType(NodeType.QUEUE.getCode())
                    .actorType(ActorType.CONSUMER.getCode())
                    .build();

            c = new QueueConsumer(this, actorInfo, messageListenerHolder, concurrentNum);

            addConsumer(c);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return c;
    }


    @Override
    public ITopicConsumer createTopicConsumer(String topicName) {
        return createTopicConsumer(topicName, null);
    }

    @Override
    public ITopicConsumer createTopicConsumer(String topicName, MessageListenerHolder messageListenerHolder) {
        ITopicConsumer c = null;

        try {
            ActorInfo actorInfo = ActorInfoFactory.builder()
                    .clientName(name)
                    .brokerURL(brokerURL)
                    .username(username)
                    .password(password)
                    .nodename(topicName)
                    .nodeType(NodeType.TOPIC.getCode())
                    .actorType(ActorType.CONSUMER.getCode())
                    .build();

            c = new TopicConsumer(this, actorInfo, messageListenerHolder);

            addConsumer(c);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return c;
    }

    @Override
    public void close() {

        synchronized (producerLock) {
            Iterator<IProducer> pIterator = producers.iterator();
            while (pIterator.hasNext()) {
                IProducer p = pIterator.next();
                p.close();
                pIterator.remove();
            }
        }

        synchronized (consumerLock) {
            Iterator<IConsumer> cIterator = consumers.iterator();
            while (cIterator.hasNext()) {
                IConsumer c = cIterator.next();
                c.close();
                cIterator.remove();
            }
        }

        //TODO mq connection pool 回收 对应 未使用的broker 资源
        //在下次使用时，再进行注册便可
    }

    void addProduce(IProducer producer) {
        synchronized (producerLock) {
            producers.add(producer);
        }
    }

    public void destroyProduce(IProducer producer) {
        synchronized (producerLock) {
            producers.remove(producer);
        }
    }

    void addConsumer(IConsumer consumer) {
        synchronized (consumerLock) {
            consumers.add(consumer);
        }
    }

    public void destroyConsumer(IConsumer consumer) {
        synchronized (consumerLock) {
            consumers.remove(consumer);
        }
    }
}
