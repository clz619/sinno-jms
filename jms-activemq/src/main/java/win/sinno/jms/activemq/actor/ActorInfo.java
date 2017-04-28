package win.sinno.jms.activemq.actor;

/**
 * actor info
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 16:10
 */
public class ActorInfo {

    private String clientName;

    private String brokerURL;

    private String username;

    private String password;

    private String nodename;

    /**
     * {@link NodeType}
     */
    private int nodeType;
    /**
     * {@link ActorType}
     */
    private int actorType;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNodename() {
        return nodename;
    }

    public void setNodename(String nodename) {
        this.nodename = nodename;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public int getActorType() {
        return actorType;
    }

    public void setActorType(int actorType) {
        this.actorType = actorType;
    }
}
