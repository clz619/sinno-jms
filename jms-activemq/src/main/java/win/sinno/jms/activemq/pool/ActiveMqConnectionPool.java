package win.sinno.jms.activemq.pool;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import win.sinno.common.util.IntrospectionSupport;
import win.sinno.jms.activemq.configs.LoggerConfigs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * active mq connection pool
 * <p>
 * support(amqp,stomp,tcp)
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 15:27
 */
public class ActiveMqConnectionPool {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private Map<String, PooledConnectionFactory> pools = new HashMap<>();

    private ActiveMqConnectionPool() {
    }

    private static final class ActiveMqConnectPoolHolder {
        private static final ActiveMqConnectionPool HOLDER = new ActiveMqConnectionPool();
    }

    public static final ActiveMqConnectionPool getInstance() {
        return ActiveMqConnectPoolHolder.HOLDER;
    }

    public synchronized void register(MqConnectionKey connectionKey, Properties properties) {


        if (pools.containsKey(connectionKey.getPoolKey())) {
            remove(connectionKey.getPoolKey());
        }

        String brokerURL = connectionKey.getBrokerURL();
        URI uri = null;
        try {
            uri = new URI(brokerURL);
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }

        // scheme
        String scheme = uri.getScheme();

        PooledConnectionFactory pcf = null;

        if (scheme.contains("amqp")) {

            pcf = AmqpPooledConnectionFactory.createPooledConnectionFactory(brokerURL, properties);

        } else if (scheme.contains("stomp")) {

            pcf = StompPooledConnectionFactory.createPooledConnectionFactory(brokerURL, properties);

        } else {

            pcf = TcpPooledConnectionFactory.createPooledConnectionFactory(brokerURL, properties);
        }

        if (properties != null) {
            // pooled connectin properties wrap

            Set<Map.Entry<Object, Object>> s = properties.entrySet();
            Map<String, Object> p = new HashMap<>(properties.size());

            for (Iterator<Map.Entry<Object, Object>> iter = s.iterator(); iter.hasNext(); ) {
                Map.Entry<Object, Object> enty = iter.next();
                p.put(enty.getKey().toString(), enty.getValue());
            }

            IntrospectionSupport.setProperties(pcf, p, "");
        }

        pcf.start();

        pools.put(connectionKey.getPoolKey(), pcf);
    }


    public synchronized void remove(String key) {
        PooledConnectionFactory pcf = pools.get(key);

        if (pcf != null) {
            pcf.stop();

            pools.remove(key);
        }
    }

    public synchronized PooledConnectionFactory get(MqConnectionKey connectionKey) {

        String key = connectionKey.getPoolKey();

        return pools.get(key);
    }

}
