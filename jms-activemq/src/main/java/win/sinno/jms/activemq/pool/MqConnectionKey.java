package win.sinno.jms.activemq.pool;

import win.sinno.jms.activemq.actor.ActorInfo;

/**
 * mq connection key
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 16:28
 */
public class MqConnectionKey {

    private String clientName;

    private String brokerURL;

    private String username;

    private String password;


    private volatile String poolKey;

    private volatile String connKey;

    public MqConnectionKey(ActorInfo actorInfo) {
        this(actorInfo.getClientName(), actorInfo.getBrokerURL(), actorInfo.getUsername(), actorInfo.getPassword());
    }

    public MqConnectionKey(String clientName, String brokerURL, String username, String password) {
        this.clientName = clientName;
        this.brokerURL = brokerURL;
        this.username = username;
        this.password = password;

        this.poolKey = clientName + "://" + brokerURL;
        this.connKey = clientName + "://" + username + ":" + password + "@" + brokerURL;
    }

    public String getClientName() {
        return clientName;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPoolKey() {
        return poolKey;
    }

    public String getConnKey() {
        return connKey;
    }

}
