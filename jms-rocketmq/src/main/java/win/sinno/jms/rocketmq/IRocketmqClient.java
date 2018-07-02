package win.sinno.jms.rocketmq;

/**
 * win.sinno.jms.rocketmq.IRocketmqClient
 *
 * @author admin@chenlizhong.cn
 * @date 2018/7/2
 */
public interface IRocketmqClient {

  String getNamesrvAddr();

  void setNamesrvAddr(String namesrvAddr);

  String getGroup();

  void setGroup(String group);
  
}
