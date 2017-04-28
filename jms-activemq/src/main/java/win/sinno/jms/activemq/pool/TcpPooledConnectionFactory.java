package win.sinno.jms.activemq.pool;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import java.util.Properties;

/**
 * tcp pooled connection factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/20 13:38
 */
public final class TcpPooledConnectionFactory {

    private TcpPooledConnectionFactory() {
    }

    public static PooledConnectionFactory createPooledConnectionFactory(String brokerURL, Properties properties) {
        PooledConnectionFactory pcf = new PooledConnectionFactory();
        //default tcp protocol
        ActiveMQConnectionFactory amqcf = new ActiveMQConnectionFactory(brokerURL);

        if (properties != null) {
            amqcf.buildFromProperties(properties);
        }

        pcf.setConnectionFactory(amqcf);

        return pcf;
    }
}
