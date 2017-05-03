package win.sinno.jms.aliyun.ons;

import win.sinno.jms.api.IClient;
import win.sinno.jms.api.IConsumer;
import win.sinno.jms.api.IProducer;
import win.sinno.jms.api.MessageListenerHolder;

/**
 * ons client interface
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 10:15
 */
public interface IOnsClient extends IClient {

    String getName();

    void setName(String name);

    String getAccessKey();

    void setAccessKey(String accessKey);

    String getSecretKey();

    void setSecretKey(String secretKey);

    String getTopic();

    void setTopic(String topic);

    String getProducerId();

    void setProducerId(String producerId);

    String getConsumerId();

    void setConsumerId(String consumerId);

    String getSendMsgTimeoutMillis();

    void setSendMsgTimeoutMillis(String sendMsgTimeoutMillis);

    String getTag();

    void setTag(String tag);

    String getOnsAddr();

    void setOnsAddr(String onsAddr);

    /**
     * topic producer
     *
     * @return
     */
    IProducer createTopicProducer();

    /**
     * create topic producer
     *
     * @param messageListenerHolder
     * @return
     */
    IConsumer createTopicConsumer(MessageListenerHolder messageListenerHolder);
}
