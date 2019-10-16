package test;

import com.mq.model.message.Message;
import com.mq.model.message.ProducerAckMessage;
import com.mq.producer.MqProducer;

public class ProducerTest1 {
    public static void main(String[] args) throws InterruptedException {
        MqProducer producer = new MqProducer("127.0.0.1:18888", "MQ-Topic-1");
        producer.setClusterId("MQCluster");

        producer.init();
        producer.start();

        System.out.println("************************MQProducer1 消息发送开始***********************");

        for (int i = 0; i < 100; i++) {
            Message message = new Message();
            String str = "Hello AvatarMQ From Producer1[" + i + "]";
            message.setBody(str.getBytes());
            ProducerAckMessage result = producer.delivery(message);
            if (result.getStatus() == (ProducerAckMessage.SUCCESS)) {
                System.out.printf("MQProducer1 发送消息编号:%s，ack结果:%s\n", result.getMsgId(), result.getAck());
            }

            Thread.sleep(100);
        }

        producer.shutdown();
        System.out.println("************************MQProducer1 消息发送完毕************************");
    }
}
