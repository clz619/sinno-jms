package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * consumer node holder
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 17:50
 */
public class ProducerHolder extends NodeHolder {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private MessageProducer producer;

    public ProducerHolder(Connection connection, Session session, MessageProducer producer) {

        super(connection, session);

        this.producer = producer;
    }

    public MessageProducer getProducer() {
        return producer;
    }

    public void setProducer(MessageProducer producer) {
        this.producer = producer;
    }

    public synchronized void close() {
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        super.close();
    }
}
