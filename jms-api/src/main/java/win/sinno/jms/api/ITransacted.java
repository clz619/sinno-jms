package win.sinno.jms.api;

import javax.jms.JMSException;

/**
 * transacted
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/24 15:59
 */
public interface ITransacted {

    //提交
    void commit() throws JMSException;

    //回滚
    void rollback() throws JMSException;
}
