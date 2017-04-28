package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;

import javax.jms.*;

/**
 * consumer node holder
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 17:50
 */
public class ConsumerHolder extends NodeHolder {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private MessageConsumer consumer;

    private MessageListener messageListener;

    public ConsumerHolder(Connection connection, Session session, MessageConsumer consumer) {
        super(connection, session);

        this.consumer = consumer;
    }

    public ConsumerHolder(Connection connection, Session session, MessageConsumer consumer, MessageListener messageListener) {
        this(connection, session, consumer);

        this.messageListener = messageListener;
    }


    public MessageConsumer getConsumer() {
        return consumer;
    }

    void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    void setMessageListener(MessageListener messageListener) throws JMSException {
        this.messageListener = messageListener;

        if (this.consumer != null) {
            consumer.setMessageListener(messageListener);
        }
    }

    public synchronized void close() {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        super.close();
    }
}
