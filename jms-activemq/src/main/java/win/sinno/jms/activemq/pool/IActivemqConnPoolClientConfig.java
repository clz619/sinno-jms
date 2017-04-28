package win.sinno.jms.activemq.pool;

/**
 * activemq client config
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/19 17:58
 */
public interface IActivemqConnPoolClientConfig {

    /**
     * maxIdelPerKey
     * maxTotalPerKey
     *
     * @return
     */
    int getMaxConnections();

    void setMaxConnections(int maxConnections);

    int getMaximumActiveSessionPerConnection();

    void setMaximumActiveSessionPerConnection(int maximumActiveSessionPerConnection);

    boolean isBlockIfSessionPoolIsFull();

    void setBlockIfSessionPoolIsFull(boolean blockIfSessionPoolIsFull);

    long getBlockIfSessionPoolIsFullTimeout();

    void setBlockIfSessionPoolIsFullTimeout(long blockIfSessionPoolIsFullTimeout);

    int getIdleTimeout();

    void setIdleTimeout(int idleTimeout);

    long getExpiryTimeout();

    void setExpiryTimeout(long expiryTimeout);

    boolean isCreateConnectionOnStartup();

    void setCreateConnectionOnStartup(boolean createConnectionOnStartup);

    boolean isUseAnonymousProducers();

    void setUseAnonymousProducers(boolean useAnonymousProducers);

    boolean isReconnectOnException();

    void setReconnectOnException(boolean reconnectOnException);

}
