package win.sinno.jms.activemq.pool;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;

import java.util.Properties;

/**
 * amqp
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/20 10:17
 */
public class AmqpPooledConnectionFactory {

    public static PooledConnectionFactory createPooledConnectionFactory(String brokerURL, Properties properties) {
        PooledConnectionFactory pcf = new PooledConnectionFactory();
        JmsConnectionFactory jcf = new JmsConnectionFactory(brokerURL);

        setJmsConnectionFactoryProp(jcf, properties);

        pcf.setConnectionFactory(jcf);

        return pcf;
    }

    // TODO properties to setter with connection factory
    private static void setJmsConnectionFactoryProp(JmsConnectionFactory jcf, Properties properties) {
        // TODO

        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        jcf.setUsername(username);
        jcf.setPassword(password);

    }

}
