package win.sinno.jms.aliyun.ons;

import org.slf4j.Logger;
import win.sinno.jms.aliyun.ons.actor.TopicConsumer;
import win.sinno.jms.aliyun.ons.actor.TopicProducer;
import win.sinno.jms.aliyun.ons.configs.LoggerConfigs;
import win.sinno.jms.api.IClientConfig;
import win.sinno.jms.api.IConsumer;
import win.sinno.jms.api.IProducer;
import win.sinno.jms.api.MessageListenerHolder;

import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ons client
 * <p>
 * document:
 * https://help.aliyun.com/document_detail/29547.html?spm=5176.doc29548.6.583.J8H7DQ
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/2 15:18
 */
public class OnsClient extends OnsClientAdapter {

    private static final Logger LOG = LoggerConfigs.ONS_LOG;

    private String name;

    private String accessKey;

    private String secretKey;

    private String onsAddr;

    private String topic;

    private String producerId;

    private String consumerId;

    private String sendMsgTimeoutMillis;

    private String tag;

    private Properties properties;

    private IClientConfig clientConfig;

    // consumers
    private CopyOnWriteArrayList<IConsumer> consumers = new CopyOnWriteArrayList<IConsumer>();

    // producers
    private CopyOnWriteArrayList<IProducer> producers = new CopyOnWriteArrayList<IProducer>();

    private Object consumerLock = new Object();

    private Object producerLock = new Object();

    OnsClient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getSendMsgTimeoutMillis() {
        return sendMsgTimeoutMillis;
    }

    public void setSendMsgTimeoutMillis(String sendMsgTimeoutMillis) {
        this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOnsAddr() {
        return onsAddr;
    }

    public void setOnsAddr(String onsAddr) {
        this.onsAddr = onsAddr;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public IClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }


    /**
     * topic producer
     *
     * @return
     */
    @Override
    public IProducer createTopicProducer() {
        TopicProducer producer = new TopicProducer(properties);
        synchronized (producerLock) {
            producers.add(producer);
        }
        return producer;
    }

    /**
     * create topic producer
     *
     * @param messageListenerHolder
     * @return
     */
    @Override
    public IConsumer createTopicConsumer(MessageListenerHolder messageListenerHolder) {
        TopicConsumer consumer = new TopicConsumer(properties, messageListenerHolder);
        synchronized (consumerLock) {
            consumers.add(consumer);
        }
        return consumer;
    }


    public void close() {

        synchronized (producerLock) {
            Iterator<IProducer> producerIt = producers.iterator();

            while (producerIt.hasNext()) {
                IProducer producer = producerIt.next();
                producer.close();
                producers.remove(producer);
            }
        }

        synchronized (consumerLock) {
            Iterator<IConsumer> consumerIt = consumers.iterator();

            while (consumerIt.hasNext()) {
                IConsumer consumer = consumerIt.next();
                consumer.close();
                consumers.remove(consumer);
            }
        }
    }

}
