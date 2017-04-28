package win.sinno.jms.activemq.pool;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * stomp
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/20 10:23
 */
public class StompPooledConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StompPooledConnectionFactory.class);

    public static PooledConnectionFactory createPooledConnectionFactory(String brokerURL, Properties properties) {

        URI uri = null;
        try {
            uri = new URI(brokerURL);
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }

        // scheme
        String scheme = uri.getScheme();

        PooledConnectionFactory pcf = new PooledConnectionFactory();

        StompJmsConnectionFactory sjcf = new StompJmsConnectionFactory();

        sjcf.setBrokerURI("tcp" + brokerURL.substring(scheme.length()));

        setStompJmsConnectionFactory(sjcf, properties);

        pcf.setConnectionFactory(sjcf);
        return pcf;
    }

    private static void setStompJmsConnectionFactory(StompJmsConnectionFactory sjcf, Properties properties) {
        // TODO
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        sjcf.setUsername(username);
        sjcf.setPassword(password);
    }
}
