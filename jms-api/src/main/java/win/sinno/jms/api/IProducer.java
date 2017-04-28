package win.sinno.jms.api;

import javax.jms.JMSException;
import java.util.List;

/**
 * producer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 11:54
 */
public interface IProducer extends ITransacted, ICloseable {

    /**
     * send
     *
     * @param message
     */
    boolean send(String message) throws JMSException;

    /**
     * send messages(List)
     *
     * @param messages
     */
    boolean send(List<String> messages) throws JMSException;

    /**
     * session是否开启事务
     *
     * @return
     */
    boolean isTransacted();

    /**
     * session ack mode
     *
     * @return
     */
    int getSessionAckMode();

}
