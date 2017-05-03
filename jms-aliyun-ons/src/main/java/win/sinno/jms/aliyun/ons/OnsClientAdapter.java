package win.sinno.jms.aliyun.ons;

import win.sinno.jms.api.*;

import java.util.Properties;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 10:17
 */
public abstract class OnsClientAdapter implements IOnsClient {
    @Override
    public void close() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getBrokerURL() {
        return null;
    }

    @Override
    public void setBrokerURL(String brokerURL) {

    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String password) {

    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public IClientConfig getClientConfig() {
        return null;
    }

    @Override
    public void setClientConfig(IClientConfig clientConfig) {

    }

    /**
     * create producer
     * type=0 queue
     * type=1 topic
     *
     * @param name
     * @param type
     * @return
     */
    @Override
    public IProducer createProducer(String name, Integer type) {
        return null;
    }

    @Override
    public IProducer createProducer(String name, Integer type, boolean isReuse) {
        return null;
    }

    @Override
    public IProducer createProducer(String name, Integer type, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        return null;
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName) {
        return null;
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName, boolean isReuse) {
        return null;
    }

    @Override
    public IQueueProducer createQueueProducer(String queueName, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        return null;
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName) {
        return null;
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName, boolean isReuse) {
        return null;
    }

    @Override
    public ITopicProducer createTopicProducer(String topicName, boolean isReuse, boolean isTransacted, int sessionAckMode) {
        return null;
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
        return null;
    }

    @Override
    public IQueueConsumer createQueueConsumer(String queueName) {
        return null;
    }

    @Override
    public IQueueConsumer createQueueConsumer(String queueName, MessageListenerHolder messageListenerHolder) {
        return null;
    }

    @Override
    public ITopicConsumer createTopicConsumer(String topicName) {
        return null;
    }

    @Override
    public ITopicConsumer createTopicConsumer(String topicName, MessageListenerHolder messageListenerHolder) {
        return null;
    }
}
