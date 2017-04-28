package win.sinno.jms.activemq;

import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.activemq.jms.pool.PooledSession;
import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/18 17:01
 */
public class SessionHolder {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private ActivemqClient activemqClient;

    private PooledConnection connection;

    private PooledSession session;

    public SessionHolder(ActivemqClient activemqClient, PooledConnection connection, PooledSession session) {
        this.activemqClient = activemqClient;
        this.connection = connection;
        this.session = session;
    }

    public ActivemqClient getActivemqClient() {
        return activemqClient;
    }

    public PooledConnection getConnection() {
        return connection;
    }

    public PooledSession getSession() {
        return session;
    }

    public void close() {

        try {
            session.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        try {
            connection.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        activemqClient = null;
    }
}
