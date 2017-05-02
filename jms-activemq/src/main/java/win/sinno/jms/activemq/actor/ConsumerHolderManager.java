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
import javax.jms.MessageConsumer;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * consumer holder factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/25 09:41
 */
public class ConsumerHolderManager implements ICloseable {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActorInfo actorInfo;

    private MqConnectionKey connectionKey;

    private List<ConsumerHolder> consumerHolders = new ArrayList<ConsumerHolder>();

    private AtomicInteger holderCount = new AtomicInteger(0);

    private boolean isTransacted = NodeConfigs.DEFAULT_IS_TRANSACTED;

    private int sessionAckMode = NodeConfigs.DEFAULT_SESSION_ACK_MODE;

    public ConsumerHolderManager(ActorInfo actorInfo, MqConnectionKey connectionKey) {
        this(actorInfo, connectionKey, NodeConfigs.DEFAULT_IS_TRANSACTED, NodeConfigs.DEFAULT_SESSION_ACK_MODE);
    }

    public ConsumerHolderManager(ActorInfo actorInfo, MqConnectionKey connectionKey, boolean isTransacted, int sessionAckMode) {
        this.actorInfo = actorInfo;
        this.connectionKey = connectionKey;
        this.isTransacted = isTransacted;
        this.sessionAckMode = sessionAckMode;
    }

    public ConsumerHolder conn() {
        ConsumerHolder consumerHolder = null;

        try {
            PooledConnectionFactory pcf = ActiveMqConnectionPool.getInstance().get(connectionKey);

            Connection connection = pcf.createConnection(connectionKey.getUsername(), connectionKey.getPassword());

            Session session = connection.createSession(isTransacted, sessionAckMode);

            Destination destination = null;

            if (NodeType.QUEUE.getCode() == actorInfo.getNodeType()) {
                destination = session.createQueue(actorInfo.getNodename());
            } else {
                destination = session.createTopic(actorInfo.getNodename());
            }

            MessageConsumer consumer = session.createConsumer(destination);

            connection.start();

            consumerHolder = new ConsumerHolder(connection, session, consumer);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


        if (consumerHolder != null) {
            synchronized (connectionKey) {
                //add holder
                consumerHolders.add(consumerHolder);
            }
            holderCount.incrementAndGet();
        }


        return consumerHolder;
    }

    public void close(ConsumerHolder consumerHolder) {
        //close
        if (consumerHolder == null) {
            return;
        }

        justClose(consumerHolder);

        synchronized (connectionKey) {
            consumerHolders.remove(consumerHolder);
        }
    }

    public void close() {

        synchronized (connectionKey) {
            Iterator<ConsumerHolder> it = consumerHolders.iterator();

            while (it.hasNext()) {

                ConsumerHolder consumerHolder = it.next();

                justClose(consumerHolder);

                it.remove();
            }
        }
    }

    private void justClose(ConsumerHolder consumerHolder) {

        synchronized (consumerHolder) {

            if (!consumerHolder.isClose()) {
                consumerHolder.close();
                holderCount.decrementAndGet();
            }
        }
    }
}
