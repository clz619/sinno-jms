package win.sinno.jms.api;

/**
 * consumer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 11:54
 */
public interface IConsumer extends ICloseable {

    void setMessageListener(MessageListenerHolder messageListenerHolder);

    String consumer();

    String consumer(Long timeout);

}
