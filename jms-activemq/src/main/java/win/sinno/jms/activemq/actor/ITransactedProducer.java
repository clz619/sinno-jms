package win.sinno.jms.activemq.actor;

import win.sinno.jms.api.ITransacted;

import javax.jms.Destination;


/**
 * transacted producer
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 16:50
 */
public interface ITransactedProducer extends ITransacted {

    void send(Destination destination, String msg);

}
