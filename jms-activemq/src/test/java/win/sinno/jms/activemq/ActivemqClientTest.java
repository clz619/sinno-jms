package win.sinno.jms.activemq;

import org.junit.Test;
import org.slf4j.Logger;
import win.sinno.jms.activemq.configs.LoggerConfigs;
import win.sinno.jms.api.*;

import javax.jms.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * client test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/4/12 16:53
 */
public class ActivemqClientTest {

    private static final Logger LOG = LoggerConfigs.ACMQ_LOG;

    private Map<String, String> map = new HashMap<>();

    {
        map.put("tcp", "failover:(tcp://127.0.0.1:61616)");
        map.put("amqp", "amqp://127.0.0.1:5672");
        map.put("stomp", "stomp://127.0.0.1:61613");
        map.put("mqtt", "mqtt://127.0.0.1:1883");
        map.put("ws", "ws://127.0.0.1:61614");

        map.put("nio", "failover:(nio://127.0.0.1:61608)");
    }

    private void testProducer(String protocol, int num) throws Exception {
        String brokerURL = map.get(protocol);

        String queueName = protocol;

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .build();

        IQueueProducer p = activemqClient.createQueueProducer(queueName, true);

        for (int i = 1; i <= num; i++) {
            p.send("" + i);
        }

    }


    public void testConsumer(String protocol) {
        String brokerURL = map.get(protocol);

        String queueName = protocol;

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .build();

        MessageListener messageListener = new MessageListener() {

            private AtomicInteger i = new AtomicInteger();

            @Override
            public void onMessage(Message message) {

                TextMessage txtMsg = (TextMessage) message;
                int in = i.incrementAndGet();

                try {
                    System.out.println(in + ":" + txtMsg.getText());

                    Thread.sleep(1000l);
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        MessageListenerHolder<MessageListener> mlh = new MessageListenerHolder<>(messageListener);

        IConsumer c = activemqClient.createQueueConsumer(queueName, mlh);


    }

    /**
     * producer带log：
     * protocol:tcp producer num:10000 use ts:12074ms
     * <p>
     * producer不带log：
     * protocol:tcp producer num:10000 use ts:8720ms
     * protocol:tcp producer num:10000 use ts:8128ms
     * protocol:tcp producer num:10000 use ts:8230ms
     *
     * @throws InterruptedException
     */
    @Test
    public void testTcpClient() throws InterruptedException, Exception {
        String protocol = "tcp";
        int num = 100;
        long b = System.currentTimeMillis();

        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        for (int i = 0; i < 100; ) {
            testProducer(protocol, 10);
            Thread.sleep(20000);
        }

        Thread.sleep(2000000);
    }

    @Test
    public void testTcpClientConsumer() throws InterruptedException {

        String protocol = "tcp";

        testConsumer(protocol);

        Thread.sleep(1000000l);
    }

    @Test
    public void testProducerReconnect() throws InterruptedException {
        String brokerURL = "failover:(nio://127.0.0.1:61608)";

        String queueName = "reconnn";

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .build();

        //queue producer
        IProducer p = activemqClient.createQueueProducer(queueName, true);

        for (int i = 0; i < 100; i++) {
            try {
                String msg = new Date() + ":" + i;
                // 连接断开，或者未连接的时候，会断开
                p.send(msg);

                System.out.println("send msg:[" + msg + "]");

                Thread.sleep(2000l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Thread.sleep(1000 * 60 * 60l);
    }

    @Test
    public void testProducerFr() throws InterruptedException {
        String brokerURL = "failover:(tcp://127.0.0.1:61616)";

        String queueName = "fr";

        ActivemqClientPoolConfig config = new ActivemqClientPoolConfig();
        config.setMaxConnections(20);
        config.setMinEvictableIdleTimeMillis(1000 * 10);

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .clientConfig(config)
                .build();
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage tm = (TextMessage) message;

                    LOG.info("consumer:{}", tm.getText());
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        };
        MessageListenerHolder<MessageListener> mlh = new MessageListenerHolder<>(messageListener);

        IConsumer c = activemqClient.createQueueConsumer(queueName, mlh);


        //queue producer
        IProducer p = activemqClient.createQueueProducer(queueName, true);

        for (int i = 0; i < 1000 * 60 * 60; i++) {
            try {
                String msg = new Date() + ":" + i;
                // 连接断开，或者未连接的时候，会断开
                p.send(msg);

                LOG.info("send msg:{}", new Object[]{msg});

                Thread.sleep(20000l);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }


        Thread.sleep(1000 * 60 * 60l);
    }

    @Test
    public void testConsumerReconnect() throws InterruptedException {

        String brokerURL = "failover:(nio://127.0.0.1:61608)";

        String queueName = "nio";

        ActivemqClientPoolConfig poolConfig = new ActivemqClientPoolConfig();
        poolConfig.setMaxConnections(4);
        poolConfig.setCreateConnectionOnStartup(false);
        poolConfig.setJmxEnabled(false);

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .name("test-c")
                .brokerURL(brokerURL)
                .clientConfig(poolConfig)
                .build();


        IConsumer c1 = activemqClient.createQueueConsumer(queueName, new MessageListenerHolder(new MessageListener() {

            private AtomicInteger i = new AtomicInteger();

            @Override
            public void onMessage(Message message) {

                TextMessage txtMsg = (TextMessage) message;
                int in = i.incrementAndGet();

                try {
                    String jmsMsgId = txtMsg.getJMSMessageID();
                    System.out.println("c1:" + in + ":(" + jmsMsgId + "):" + txtMsg.getText());
                    Thread.sleep(500l);
//                    throw new IllegalArgumentException("aaaaaaaa");

                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
        IConsumer c2 = activemqClient.createQueueConsumer(queueName, new MessageListenerHolder(new MessageListener() {

            private AtomicInteger i = new AtomicInteger();

            @Override
            public void onMessage(Message message) {

                TextMessage txtMsg = (TextMessage) message;
                int in = i.incrementAndGet();

                try {
                    System.out.println("c2:" + in + ":" + txtMsg.getText());
                    Thread.sleep(500l);
//                    throw new IllegalArgumentException("aaaaaaaa");
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
        IConsumer c3 = activemqClient.createQueueConsumer(queueName, new MessageListenerHolder(new MessageListener() {

            private AtomicInteger i = new AtomicInteger();

            @Override
            public void onMessage(Message message) {

                TextMessage txtMsg = (TextMessage) message;
                int in = i.incrementAndGet();

                try {
                    System.out.println("c3:" + in + ":" + txtMsg.getText());
                    Thread.sleep(500l);
//                    throw new IllegalArgumentException("aaaaaaaa");
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
        Thread.sleep(1000000l);
    }

    /**
     * protocol:amqp producer num:10000 use ts:6455ms
     *
     * @throws InterruptedException
     */
    @Test
    public void testAmqpClient() throws InterruptedException, Exception {
        String protocol = "amqp";
        int num = 10000;
        long b = System.currentTimeMillis();
        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(2000);
    }

    /**
     * protocol:stomp producer num:10000 use ts:11131ms
     *
     * @throws InterruptedException
     */
    @Test
    public void testStompClient() throws InterruptedException, Exception {
        String protocol = "stomp";
        int num = 10000;
        long b = System.currentTimeMillis();
        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(2000);
    }

    /**
     * mqtt 为ibm单独开发的，非jms协议，需要单独使用
     *
     * @throws InterruptedException
     */
    @Test
    public void testMqttClient() throws InterruptedException, Exception {

        String protocol = "mqtt";
        int num = 10000;
        long b = System.currentTimeMillis();
        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(2000);
    }

    /**
     * http://blog.csdn.net/czp11210/article/details/8837913
     * <p>
     * publisher ws
     *
     * @throws InterruptedException
     */
    @Test
    public void testWsClient() throws InterruptedException, Exception {
        String protocol = "ws";
        int num = 10000;
        long b = System.currentTimeMillis();
        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(2000);
    }

    /**
     * protocol:tcp producer num:10000 use ts:8616ms
     *
     * @throws InterruptedException
     */
    @Test
    public void testTcpClient2() throws InterruptedException {
        String protocol = "tcp";
        int num = 10000;
        long b = System.currentTimeMillis();

        testConsumer(protocol);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(100000);
    }

    /**
     * nio client
     * protocol:nio producer num:10000 use ts:11374ms
     * protocol:nio producer num:10000 use ts:9004ms
     *
     * @throws InterruptedException
     */
    @Test
    public void testNioClient() throws InterruptedException, Exception {
        String protocol = "nio";
        int num = 10;
        long b = System.currentTimeMillis();
        testProducer(protocol, num);
        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(100000);
    }

    @Test
    public void testUnreuseProducer() throws Exception {
        String protocol = "nio";
        int num = 10;
        long b = System.currentTimeMillis();
        String brokerURL = map.get(protocol);

        String queueName = protocol;

        ActivemqClientPoolConfig clientConfig = new ActivemqClientPoolConfig();
        clientConfig.setMaxConnections(8);
        clientConfig.setCreateConnectionOnStartup(false);

        ActivemqClient activemqClient = ActivemqClientFactory.builder()
                .name("client1")
                .brokerURL(brokerURL)
                .clientConfig(clientConfig)
                .build();


//        ActivemqClient activemqClient2 = ActivemqClientFactory.builder()
//                .name("client2")
//                .brokerURL(brokerURL)
//                .build();


        IQueueProducer p = activemqClient.createQueueProducer(queueName, true);

        IQueueProducer p2 = activemqClient.createQueueProducer("tcp", true);

        for (int i = 1; i <= num; i++) {
            p.send("" + i);
            p2.send("" + i);
            Thread.sleep(2000);
        }

        long e = System.currentTimeMillis();

        LOG.info("protocol:{} producer num:{} use ts:{}ms", new Object[]{protocol, num, e - b});

        Thread.sleep(100000);
    }


    @Test
    public void testPoolConnEvict() throws InterruptedException {
        String brokerURL = "failover:(nio://127.0.0.1:61608)";

        String queueName = "evict";

        ActivemqClientPoolConfig clientConfig = new ActivemqClientPoolConfig();
        clientConfig.setIdleTimeout(5000);

        // client config
        IClient client = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .clientConfig(clientConfig)
                .build();

        //queue producer
        IProducer p = client.createQueueProducer(queueName, true);

        for (int i = 0; i < 2; i++) {
            try {
                String msg = new Date() + ":" + i;
                // 连接断开，或者未连接的时候，会断开
                p.send(msg);

                System.out.println("send msg:[" + msg + "]");

                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        p.close();

        // 1. conn回收

        Thread.sleep(30000l);

        MessageListener msgListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {

                TextMessage t = (TextMessage) message;
                try {
                    System.out.println(t.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        IConsumer c = client.createQueueConsumer(queueName);

        p = client.createQueueProducer(queueName, true);

        for (int i = 0; i < 20; i++) {
            try {
                String msg = new Date() + ":" + i;
                // 连接断开，或者未连接的时候，会断开
                p.send(msg);

                System.out.println("send msg:[" + msg + "]");

                Thread.sleep(1000l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(30000l);


        c.close();
        p.close();

        // 2. conn(c,p) 回收

        System.out.println("end!");

        Thread.sleep(1000000l);
    }


    //topic
    @Test
    public void testTopic() {
        //test topic

        String brokerURL = "failover:(nio://127.0.0.1:61608)";

        String topicName = "topic:1";

        IClientConfig clientConfig = new ActivemqClientPoolConfig();

        ActivemqClient client = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .clientConfig(clientConfig)
                .build();

        ITopicProducer topicProducer = client.createTopicProducer(topicName, true);

        ITopicConsumer c1 = client.createTopicConsumer(topicName, new MessageListenerHolder(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage txtMsg = (TextMessage) message;

                    LOG.info("c111 get msg:{}", new Object[]{txtMsg.getText()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        ITopicConsumer c2 = client.createTopicConsumer(topicName, new MessageListenerHolder(new MessageListener() {
            @Override
            public void onMessage(Message message) {

                try {
                    TextMessage txtMsg = (TextMessage) message;

                    LOG.info("c222 get msg:{}", new Object[]{txtMsg.getText()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        try {
            for (int i = 0; i < 5; i++) {
                topicProducer.send(i + "");

                Thread.sleep(1000l);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        c2.close();

        try {
            for (int i = 5; i < 10; i++) {
                topicProducer.send(i + "");

                Thread.sleep(1000l);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        ITopicConsumer c3 = client.createTopicConsumer(topicName, new MessageListenerHolder(new MessageListener() {
            @Override
            public void onMessage(Message message) {

                try {
                    TextMessage txtMsg = (TextMessage) message;

                    LOG.info("c333 get msg:{}", new Object[]{txtMsg.getText()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        try {
            for (int i = 10; i < 15; i++) {
                topicProducer.send(i + "");

                Thread.sleep(1000l);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


    }

    //tr

    @Test
    public void testTransacted() {
        String brokerURL = "failover:(nio://127.0.0.1:61608)";

        String queue = "transacted";

        IClientConfig clientConfig = new ActivemqClientPoolConfig();

        ActivemqClient client = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .clientConfig(clientConfig)
                .build();

        IQueueProducer queueProducer = client.createQueueProducer(queue, false, true, Session.AUTO_ACKNOWLEDGE);

        List<String> msgs = new ArrayList<>();

        msgs.add("hello");
        msgs.add("world");
        msgs.add("!");
        msgs.add("coder");

        try {
            queueProducer.send(msgs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IQueueProducer tp2 = client.createQueueProducer(queue, true, true, Session.AUTO_ACKNOWLEDGE);
        try {
            tp2.send("nihao");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
