package com.mq.producer;

import java.util.concurrent.atomic.AtomicLong;

import com.mq.core.MqAction;
import com.mq.model.message.Message;
import com.mq.model.message.ProducerAckMessage;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.netty.MessageProcessor;
import com.mq.netty.handler.producer.MessageProducerHandler;

/**
 * 消息生产者
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年10月12日 上午10:25:09
 */
public class MqProducer extends MessageProcessor implements MqAction {
    /** 是否连接，是否运行中标志位 */
    private boolean brokerConnect = false;
    private boolean running = false;
    /**
     * 连接消息转发服务器broker的ip地址
     */
    private String brokerServerAddress;
    /**
     * 消息主题信息
     */
    private String topic;
    private String defaultClusterId = "";
    private String clusterId = "";
    private AtomicLong msgId = new AtomicLong();

    /**
     * 构造方法
     * @param brokerServerAddress
     * @param topic
     */
    public MqProducer(String brokerServerAddress, String topic) {
        super(brokerServerAddress);
        this.brokerServerAddress = brokerServerAddress;
        this.topic = topic;
    }
    
    /**
     * 没有连接上消息转发服务器broker，返回应答失败消息
     * 
     * @return ProducerAckMessage
     * @date: 2019年9月18日 下午5:20:32
     */
    private ProducerAckMessage checkMode() {
        if (!brokerConnect) {
            return generateFailMessage();
        }
        return null;
    }

    /**
     * 生成生产者失败应答消息
     * 
     * @return ProducerAckMessage
     * @date: 2019年10月12日 下午3:37:46
     */
    private ProducerAckMessage generateFailMessage() {
        ProducerAckMessage ack = new ProducerAckMessage();
        ack.setStatus(ProducerAckMessage.FAIL);
        return ack;
    }

    /**
     * 启动生产者
     */
    @Override
    public void start() {
        super.getMessageConnectFactory().connect();
        brokerConnect = true;
        running = true;
    }

    /**
     * 连接消息转发服务器broker，设定生产者消息处理钩子，处理broker过来的消息应答
     */
    @Override
    public void init() {
        ProducerHookMessageEvent hook = new ProducerHookMessageEvent();
        hook.setBrokerConnect(brokerConnect);
        hook.setRunning(running);
        super.getMessageConnectFactory().setMessageHandler(new MessageProducerHandler(this, hook));
    }

    /**
     * 关闭生产者
     */
    @Override
    public void shutdown() {
        if (running) {
            running = false;
            super.getMessageConnectFactory().close();
            // super.closeMessageConnectFactory();
        }
    }

    /**
     * 投递消息
     * 
     * @param message
     * @return ProducerAckMessage
     * @date: 2019年9月26日 下午2:40:16
     */
    public ProducerAckMessage delivery(Message message) {
        // 连接失败，直接返回错误
        ProducerAckMessage result = checkMode();
        if (result != null) {
            return result;
        }

        // 连接成功设置消息
        message.setTopic(topic);
        message.setTimeStamp(System.currentTimeMillis());

        RequestMessage request = new RequestMessage();
        // 消息唯一id自增生成
        request.setMsgId(String.valueOf(msgId.incrementAndGet()));
        request.setMsgParams(message);
        request.setMsgType(MessageType.MQMessage);
        request.setMsgSource(MessageSource.MQProducer);
        message.setMsgId(request.getMsgId());

        ResponseMessage response = (ResponseMessage) sendMessageSync(request);
        if (response == null) {
            return generateFailMessage();
        }

        result = (ProducerAckMessage) response.getMsgParams();
        return result;
    }

    public boolean isBrokerConnect() {
        return brokerConnect;
    }

    public void setBrokerConnect(boolean brokerConnect) {
        this.brokerConnect = brokerConnect;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getBrokerServerAddress() {
        return brokerServerAddress;
    }

    public void setBrokerServerAddress(String brokerServerAddress) {
        this.brokerServerAddress = brokerServerAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDefaultClusterId() {
        return defaultClusterId;
    }

    public void setDefaultClusterId(String defaultClusterId) {
        this.defaultClusterId = defaultClusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public AtomicLong getMsgId() {
        return msgId;
    }

    public void setMsgId(AtomicLong msgId) {
        this.msgId = msgId;
    }

}
