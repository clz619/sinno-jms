package win.sinno.jms.aliyun.ons.actor;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import win.sinno.jms.aliyun.ons.configs.PropertyKeyConst;
import win.sinno.jms.api.IConsumer;
import win.sinno.jms.api.MessageListenerHolder;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ons topic consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/2 16:36
 */
public class TopicConsumer implements IConsumer {

    private String accessKey;

    private String secretKey;

    private String topic;

    private String consumerId;

    private String tags;

    private String onsAddr;

    private Properties properties;

    private Consumer consumer;

    private AtomicBoolean runFlag = new AtomicBoolean(false);

    public TopicConsumer(Properties properties, MessageListenerHolder messageListenerHolder) {
        accessKey = properties.getProperty(PropertyKeyConst.accessKey);
        secretKey = properties.getProperty(PropertyKeyConst.secretKey);
        topic = properties.getProperty(PropertyKeyConst.topic);
        consumerId = properties.getProperty(PropertyKeyConst.consumerId);
        tags = properties.getProperty(PropertyKeyConst.tag);
        onsAddr = properties.getProperty(PropertyKeyConst.onsAddr);

        this.properties = properties;

        consumer = ONSFactory.createConsumer(properties);

        setMessageListener(messageListenerHolder);
    }

    @Override
    public void close() {
        if (consumer != null && runFlag.compareAndSet(true, false)) {
            consumer.shutdown();
        }
    }

    @Override
    public void setMessageListener(MessageListenerHolder messageListenerHolder) {
        if (messageListenerHolder == null) {
            return;
        }
        Object holder = messageListenerHolder.get();
        MessageListener messageListener = null;

        if (holder != null && holder instanceof MessageListener) {
            messageListener = (MessageListener) holder;
        }

        if (consumer != null && messageListener != null && runFlag.compareAndSet(false, true)) {
            consumer.start();

            // msg listener
            consumer.subscribe(topic, tags, messageListener);
        }
    }

    @Override
    public String consumer() {
        return null;
    }

    @Override
    public String consumer(Long timeout) {
        return null;
    }
}
