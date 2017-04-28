package win.sinno.jms.activemq.actor;

/**
 * TODO
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/26 10:46
 */
public class ActorInfoFactory {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String clientName;

        private String brokerURL;

        private String username;

        private String password;

        private String nodename;

        /**
         * {@link NodeType}
         */
        private int nodeType = NodeType.QUEUE.getCode();
        /**
         * {@link ActorType}
         */
        private int actorType = ActorType.PRODUCER.getCode();

        public Builder clientName(String clientName) {
            this.clientName = clientName;
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

        public Builder nodename(String nodename) {
            this.nodename = nodename;
            return this;
        }

        public Builder nodeType(int nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public Builder actorType(int actorType) {
            this.actorType = actorType;
            return this;
        }


        /**
         * 建立
         *
         * @return
         */
        public ActorInfo build() {
            ActorInfo actorInfo = new ActorInfo();
            actorInfo.setClientName(clientName);
            actorInfo.setBrokerURL(brokerURL);
            actorInfo.setUsername(username);
            actorInfo.setPassword(password);
            actorInfo.setNodename(nodename);
            actorInfo.setNodeType(nodeType);
            actorInfo.setActorType(actorType);

            return actorInfo;
        }
    }
}
