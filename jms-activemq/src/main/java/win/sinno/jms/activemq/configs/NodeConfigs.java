package win.sinno.jms.activemq.configs;

import javax.jms.Session;

/**
 * ━━━━━━oooo━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━oooo━━━━━━
 * <p>
 * node
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/28 13:56
 */
public class NodeConfigs {

    /**
     * node. reuse flag . default false
     */
    public static boolean DEFAULT_IS_REUSE = false;

    /**
     * node. session transacted flag . default false
     */
    public static boolean DEFAULT_IS_TRANSACTED = false;

    /**
     * node. session ack mode . default auto ack
     */
    public static int DEFAULT_SESSION_ACK_MODE = Session.AUTO_ACKNOWLEDGE;
}
