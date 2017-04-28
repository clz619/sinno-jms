package win.sinno.jms.activemq.actor;

import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;

import javax.jms.Connection;
import javax.jms.Session;

/**
 * consumer node holder
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 17:50
 */
public class NodeHolder {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private Connection connection;

    private Session session;

    private boolean isClose = false;

    public NodeHolder(Connection connection, Session session) {
        this.connection = connection;
        this.session = session;
    }

    public Connection getConnection() {
        return connection;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    void setSession(Session session) {
        this.session = session;
    }

    public boolean isClose() {
        return isClose;
    }

    void setClose(boolean close) {
        isClose = close;
    }

    public synchronized void close() {

        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        isClose = true;
    }
}
