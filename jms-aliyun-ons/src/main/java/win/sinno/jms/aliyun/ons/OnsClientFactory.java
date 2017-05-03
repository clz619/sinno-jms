package win.sinno.jms.aliyun.ons;

import win.sinno.jms.aliyun.ons.configs.PropertyKeyConst;

import java.util.Properties;

/**
 * ons client factory
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/5/3 09:54
 */
public class OnsClientFactory {

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String name;

        private String accessKey;

        private String secretKey;

        private String topic;

        private String producerId;

        private String consumerId;

        private String sendMsgTimeoutMillis;

        private String tag;

        private String onsAddr;

        private Properties properties = new Properties();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder accessKey(String accessKey) {
            this.accessKey = accessKey;
            properties.put(PropertyKeyConst.accessKey, accessKey);
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            properties.put(PropertyKeyConst.secretKey, secretKey);
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            properties.put(PropertyKeyConst.topic, topic);
            return this;
        }

        public Builder producerId(String producerId) {
            this.producerId = producerId;
            properties.put(PropertyKeyConst.producerId, producerId);
            return this;
        }

        public Builder consumerId(String consumerId) {
            this.consumerId = consumerId;
            properties.put(PropertyKeyConst.consumerId, consumerId);
            return this;
        }

        public Builder sendMsgTimeoutMillis(String sendMsgTimeoutMillis) {
            this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
            properties.put(PropertyKeyConst.sendMsgTimeoutMillis, sendMsgTimeoutMillis);
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            properties.put(PropertyKeyConst.tag, tag);
            return this;
        }

        public Builder onsAddr(String onsAddr) {
            this.onsAddr = onsAddr;
            properties.put(PropertyKeyConst.onsAddr, onsAddr);
            return this;
        }

        public Builder properties(Properties properties) {
            if (properties != null) {
                this.properties.putAll(properties);
            }
            return this;
        }

        public OnsClient build() {
            OnsClient onsClient = new OnsClient();
            onsClient.setName(name);
            onsClient.setAccessKey(accessKey);
            onsClient.setSecretKey(secretKey);
            onsClient.setTopic(topic);
            onsClient.setProducerId(producerId);
            onsClient.setSendMsgTimeoutMillis(sendMsgTimeoutMillis);
            onsClient.setTag(tag);
            onsClient.setOnsAddr(onsAddr);
            onsClient.setProperties(properties);
            return onsClient;
        }
    }
}
