package win.sinno.jms.aliyun.ons.actor;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import win.sinno.jms.aliyun.ons.configs.PropertyKeyConst;
import win.sinno.jms.api.IProducer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ons topic producer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/2 15:37
 */
public class TopicProducer implements IProducer {

    private String accessKey;

    private String secretKey;

    private String topic;

    private String producerId;

    private String sendMsgTimeoutMillis;

    private String tag;

    private String onsAddr;

    private Properties properties;

    private Producer producer;

    private AtomicBoolean runFlag = new AtomicBoolean(false);

    public TopicProducer(Properties properties) {
        accessKey = properties.getProperty(PropertyKeyConst.accessKey);
        secretKey = properties.getProperty(PropertyKeyConst.secretKey);
        topic = properties.getProperty(PropertyKeyConst.topic);
        producerId = properties.getProperty(PropertyKeyConst.producerId);
        sendMsgTimeoutMillis = properties.getProperty(PropertyKeyConst.sendMsgTimeoutMillis);
        tag = properties.getProperty(PropertyKeyConst.tag);
        onsAddr = properties.getProperty(PropertyKeyConst.onsAddr);

        this.properties = properties;

        producer = ONSFactory.createProducer(properties);

        if (producer != null && runFlag.compareAndSet(false, true)) {
            producer.start();
        }
    }

    @Override
    public void close() {
        if (producer != null && runFlag.compareAndSet(true, false)) {
            producer.shutdown();
        }
    }

    @Override
    public void commit() throws Exception {
        //
    }

    @Override
    public void rollback() throws Exception {
        //
    }

    /**
     * send
     *
     * @param message
     */
    @Override
    public boolean send(String message) throws Exception {
        byte[] bytes = message.getBytes("UTF-8");
        Message msg = new Message(topic, tag, bytes);
        producer.send(msg);

        return true;
    }

    /**
     * send messages(List)
     *
     * @param messages
     */
    @Override
    public boolean send(List<String> messages) throws Exception {
        for (String message : messages) {
            send(message);
        }
        return true;
    }

    /**
     * session是否开启事务
     *
     * @return
     */
    @Override
    public boolean isTransacted() {
        return false;
    }

    /**
     * session ack mode
     *
     * @return
     */
    @Override
    public int getSessionAckMode() {
        return 0;
    }
}
