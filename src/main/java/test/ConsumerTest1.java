package test;

import com.mq.consumer.MqConsumer;
import com.mq.consumer.hook.ConsumeProducerMessageHook;
import com.mq.model.message.ConsumerAckMessage;
import com.mq.model.message.Message;


public class ConsumerTest1 {
    private static ConsumeProducerMessageHook hook = new ConsumeProducerMessageHook() {
        public ConsumerAckMessage hookMessage(Message message) {
            // 消费消息
            System.out.printf("MQConsumer2 收到消息编号:%s,消息内容:%s\n", message.getMsgId(),
                    new String(message.getBody()));
            ConsumerAckMessage result = new ConsumerAckMessage();
            result.setStatus(ConsumerAckMessage.SUCCESS);
            return result;
        }
    };

    public static void main(String[] args) {
        MqConsumer consumer = new MqConsumer("127.0.0.1:18888", "MQ-Topic-1", hook);
        consumer.setClusterId("MQCluster");
        consumer.setSubscribeMessage(true);
        consumer.init();
        consumer.start();

        // consumer.shutdown();
    }
}
