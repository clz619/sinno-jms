package win.sinno.jms.activemq;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import win.sinno.jms.activemq.pool.IActivemqConnPoolClientConfig;
import win.sinno.jms.api.IClientConfig;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/19 17:22
 */
public class ActivemqClientPoolConfig extends GenericKeyedObjectPoolConfig implements IClientConfig, IActivemqConnPoolClientConfig {

    //mq pool client config
    private int maxConnections = 8;

    //client config
    private int maximumActiveSessionPerConnection = 500;
    private int idleTimeout = 1000 * 30;
    private boolean blockIfSessionPoolIsFull = true;
    private long blockIfSessionPoolIsFullTimeout = -1L;
    private long expiryTimeout = 0l;
    private boolean createConnectionOnStartup = true;
    private boolean useAnonymousProducers = true;
    private boolean reconnectOnException = true;

    @Override
    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMaximumActiveSessionPerConnection() {
        return maximumActiveSessionPerConnection;
    }

    public void setMaximumActiveSessionPerConnection(int maximumActiveSessionPerConnection) {
        this.maximumActiveSessionPerConnection = maximumActiveSessionPerConnection;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public boolean isBlockIfSessionPoolIsFull() {
        return blockIfSessionPoolIsFull;
    }

    public void setBlockIfSessionPoolIsFull(boolean blockIfSessionPoolIsFull) {
        this.blockIfSessionPoolIsFull = blockIfSessionPoolIsFull;
    }

    public long getBlockIfSessionPoolIsFullTimeout() {
        return blockIfSessionPoolIsFullTimeout;
    }

    public void setBlockIfSessionPoolIsFullTimeout(long blockIfSessionPoolIsFullTimeout) {
        this.blockIfSessionPoolIsFullTimeout = blockIfSessionPoolIsFullTimeout;
    }

    public long getExpiryTimeout() {
        return expiryTimeout;
    }

    public void setExpiryTimeout(long expiryTimeout) {
        this.expiryTimeout = expiryTimeout;
    }

    public boolean isCreateConnectionOnStartup() {
        return createConnectionOnStartup;
    }

    public void setCreateConnectionOnStartup(boolean createConnectionOnStartup) {
        this.createConnectionOnStartup = createConnectionOnStartup;
    }

    public boolean isUseAnonymousProducers() {
        return useAnonymousProducers;
    }

    public void setUseAnonymousProducers(boolean useAnonymousProducers) {
        this.useAnonymousProducers = useAnonymousProducers;
    }

    public boolean isReconnectOnException() {
        return reconnectOnException;
    }

    public void setReconnectOnException(boolean reconnectOnException) {
        this.reconnectOnException = reconnectOnException;
    }
}
