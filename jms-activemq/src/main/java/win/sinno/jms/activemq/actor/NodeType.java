package win.sinno.jms.activemq.actor;

/**
 * node type
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 16:52
 */
public enum NodeType {
    /**
     * 0,队列,queue
     */
    QUEUE(0, "队列", "queue"),

    /**
     * 1,主题,topic
     */
    TOPIC(1, "主题", "topic");

    NodeType(int code, String valueCn, String valueEn) {
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
