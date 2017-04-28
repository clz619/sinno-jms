package win.sinno.jms.activemq.actor;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.activemq.configs.NodeConfigs;
import win.sinno.jms.activemq.pool.ActiveMqConnectionPool;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.ICloseable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * actor
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 17:44
 */
public class ProducerHolderManager implements ICloseable {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private List<ProducerHolder> producerHolders = new ArrayList<ProducerHolder>();

    private AtomicInteger holderCount = new AtomicInteger(0);

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    public ProducerHolderManager(ActorInfo actorInfo, MqConnectionKey connectionKey) {
        this.actorInfo = actorInfo;
        this.connectionKey = connectionKey;
    }

    public ProducerHolderManager(ActorInfo actorInfo, MqConnectionKey connectionKey, boolean isTransacted, int sessionAckMode) {
        this.actorInfo = actorInfo;
        this.connectionKey = connectionKey;
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;
    }

    public ProducerHolder conn() {

        ProducerHolder holder = null;

        PooledConnectionFactory pcf = ActiveMqConnectionPool.getInstance().get(connectionKey);

        try {
            Connection connection = pcf.createConnection(connectionKey.getUsername(), connectionKey.getPassword());

            Session session = connection.createSession(isTransacted, sessionAckMode);

            Destination destination = null;

            if (NodeType.QUEUE.getCode() == actorInfo.getNodeType()) {
                destination = session.createQueue(actorInfo.getNodename());
            } else {
                destination = session.createTopic(actorInfo.getNodename());
            }

            MessageProducer producer = session.createProducer(destination);

            // producer delivery mode (@see javax.jms.Message.DEFAULT_DELIVERY_MODE = DeliveryMode.PERSISTENT)

            connection.start();

            holder = new ProducerHolder(connection, session, producer);

        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
        }

        synchronized (connectionKey) {
            if (holder != null) {
                producerHolders.add(holder);
                holderCount.incrementAndGet();
            }
        }

        return holder;
    }

    public void close(ProducerHolder producerHolder) {

        if (producerHolder == null) {
            return;
        }

        synchronized (connectionKey) {
            if (!producerHolder.isClose()) {
                producerHolder.close();
                holderCount.decrementAndGet();
            }

            producerHolders.remove(producerHolder);
        }
    }

    public void close() {

        synchronized (connectionKey) {
            Iterator<ProducerHolder> it = producerHolders.iterator();

            while (it.hasNext()) {
                ProducerHolder producerHolder = it.next();

                if (!producerHolder.isClose()) {
                    producerHolder.close();
                    holderCount.decrementAndGet();
                }

                it.remove();
            }
        }
    }


}
