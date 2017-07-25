# sinno-jms

重新封装jms接口  

提供activemq的实现：  
快速支持ConnectionPool；  
支持多种传输方式(http、tcp、stomp、nio)。


```
  String brokerURL = "failover:(nio://127.0.0.1:61608)";

  String queue = "que";
  IClientConfig clientConfig = new ActivemqClientPoolConfig();

  ActivemqClient client = ActivemqClientFactory.builder()
                .brokerURL(brokerURL)
                .clientConfig(clientConfig)
                .build();
  #消费者
  IQueueConsumer queueConsumer = client.createQueueConsumer(queue, new MessageListenerHolder(new MessageListener() {
      @Override
      public void onMessage(Message message) {
                try {
                    TextMessage txtMsg = (TextMessage) message;

                    LOG.info("consumer get msg:{}", new Object[]{txtMsg.getText()});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
      }), 5);
      
   #生产者
  IQueueProducer queueProducer = client.createQueueProducer(queue, false, true, Session.AUTO_ACKNOWLEDGE);
  
   try {
        queueProducer.send("msg");
   } catch (Exception e) {
        e.printStackTrace();
   }

```