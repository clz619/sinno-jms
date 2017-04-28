package win.sinno.jms.activemq.actor;

/**
 * actor type
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 17:02
 */
public enum ActorType {

    /**
     * 0,生产者,producer
     */
    PRODUCER(0, "生产者", "producer"),

    /**
     * 1,消费者,consumer
     */
    CONSUMER(1, "消费者", "consumer");

    ActorType(int code, String valueCn, String valueEn) {
        this.code = code;
        this.valueCn = valueCn;
        this.valueEn = valueEn;
    }

    private int code;

    private String valueCn;

    private String valueEn;

    public int getCode() {
        return code;
    }

    public String getValueCn() {
        return valueCn;
    }

    public String getValueEn() {
        return valueEn;
    }
}

