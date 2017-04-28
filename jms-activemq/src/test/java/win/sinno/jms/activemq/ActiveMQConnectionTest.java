package win.sinno.jms.activemq;

import org.apache.activemq.util.URISupport;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * activemq connection test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/19 11:13
 */
public class ActiveMQConnectionTest {

    @Test
    public void test() throws URISyntaxException {

        String brokerURL = "failover:(tcp://10.132.173.138:30003,tcp://10.241.50.74:30002)";

        URI uri = new URI(brokerURL);
        System.out.println(uri.getScheme());
        System.out.println(uri.getSchemeSpecificPart());

        String u = "http://www.baidu.com";
        URI uri2 = new URI(u);
        System.out.println(uri2.getScheme());
        System.out.println(uri2.getSchemeSpecificPart());
        System.out.println(uri2.getPath());


        URISupport.CompositeData compositeData = URISupport.parseComposite(uri);

        compositeData.getParameters();
    }



}
