package win.sinno.jms.activemq;

import win.sinno.common.util.IntrospectionSupport;
import win.sinno.jms.activemq.pool.ActiveMqConnectionPool;
import win.sinno.jms.activemq.pool.MqConnectionKey;
import win.sinno.jms.api.IClientConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * acymq client factor
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 16:55
 */
public class ActivemqClientFactory {

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;

        private String brokerURL;

        private String username;

        private String password;

        private Properties properties;

        private IClientConfig clientConfig;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder brokerURL(String brokerURL) {
            this.brokerURL = brokerURL;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder properties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder clientConfig(IClientConfig clientConfig) {
            this.clientConfig = clientConfig;
            return this;
        }

        public ActivemqClient build() {
            ActivemqClient c = new ActivemqClient();
            c.setName(name);
            c.setBrokerURL(brokerURL);
            c.setUsername(username);
            c.setPassword(password);
            c.setProperties(properties);
            c.setClientConfig(clientConfig);

            if (properties == null) {
                properties = new Properties();
            }

            if (username != null && password != null) {
                properties.setProperty(USERNAME, username);
                properties.setProperty(PASSWORD, password);
            }

            if (clientConfig != null) {
                Map ccProp = new HashMap();
                IntrospectionSupport.getProperties(clientConfig, ccProp, null);
                if (ccProp.size() > 0) {
                    properties.putAll(ccProp);
                }
            }

            ActiveMqConnectionPool.getInstance().register(new MqConnectionKey(name, brokerURL, username, password), properties);

            return c;
        }
    }
}
