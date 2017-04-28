package win.sinno.jms.api;

import javax.jms.MessageListener;
import java.util.Properties;

/**
 * client
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 11:59
 */
public interface IClient extends ICloseable {

    // name
    String getName();

    void setName(String name);

    // broker url
    String getBrokerURL();

    void setBrokerURL(String brokerURL);

    // username
    String getUsername();

    void setUsername(String username);

    // password
    String getPassword();

    void setPassword(String password);

    // properties
    Properties getProperties();

    void setProperties(Properties properties);

    // default pool config - client config
    IClientConfig getClientConfig();

    // client
    void setClientConfig(IClientConfig clientConfig);

    /**
     * create producer
     * type=0 queue
     * type=1 topic
     *
     * @param name
     * @param type
     * @return
     */
    IProducer createProducer(String name, Integer type);

    IProducer createProducer(String name, Integer type, boolean isReuse);

    IProducer createProducer(String name, Integer type, boolean isReuse, boolean isTransacted, int sessionAckMode);

    IQueueProducer createQueueProducer(String queueName);

    IQueueProducer createQueueProducer(String queueName, boolean isReuse);

    IQueueProducer createQueueProducer(String queueName, boolean isReuse, boolean isTransacted, int sessionAckMode);

    ITopicProducer createTopicProducer(String topicName);

    ITopicProducer createTopicProducer(String topicName, boolean isReuse);

    ITopicProducer createTopicProducer(String topicName, boolean isReuse, boolean isTransacted, int sessionAckMode);

    /**
     * create consumer
     *
     * @param name
     * @param type
     * @return
     */
    IConsumer createConsumer(String name, Integer type);

    IQueueConsumer createQueueConsumer(String queueName);

    IQueueConsumer createQueueConsumer(String queueName, MessageListener messageListener);

    ITopicConsumer createTopicConsumer(String topicName);

    ITopicConsumer createTopicConsumer(String topicName, MessageListener messageListener);

}
