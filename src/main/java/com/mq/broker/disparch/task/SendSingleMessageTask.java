package com.mq.broker.disparch.task;

import com.mq.broker.SendMessageLauncher;
import com.mq.consumer.cluster.ClustersState;
import com.mq.consumer.cluster.ConsumerContext;
import com.mq.model.MessageDispatchTask;
import com.mq.model.RemoteChannelData;
import com.mq.model.message.ConsumerAckMessage;
import com.mq.model.message.Message;
import com.mq.model.message.msgenum.MessageSource;
import com.mq.model.message.msgenum.MessageType;
import com.mq.model.message.msgnet.RequestMessage;
import com.mq.model.message.msgnet.ResponseMessage;
import com.mq.util.MessageIdGenerator;
import com.mq.util.NettyUtil;

/**
 * 发送单个消息任务
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月27日 下午5:05:17
 */
public class SendSingleMessageTask implements Runnable {
    private MessageDispatchTask task;
    private SendMessageLauncher launcher = SendMessageLauncher.getInstance();

    public SendSingleMessageTask(MessageDispatchTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        Message message = task.getMessage();

        RemoteChannelData channel = ConsumerContext.selectByClusters(task.getClusters()).nextRemoteChannelData();
        while (!NettyUtil.validateChannel(channel.getChannel())) {
            ConsumerContext.addOrUpdateClustersState(task.getClusters(), ClustersState.NETWORKERR);
            // 重新获取一个channel
            channel = ConsumerContext.selectByClusters(task.getClusters()).nextRemoteChannelData();
        }

        // broker发送响应信息；向消费者发送信息
        ResponseMessage response = new ResponseMessage();
        response.setMsgSource(MessageSource.MQBroker);
        response.setMsgType(MessageType.MQMessage);
        response.setMsgParams(message);
        response.setMsgId(MessageIdGenerator.UUID.generate());

        // 发送给消费者消息，获得消费者响应
        RequestMessage request = (RequestMessage) launcher.launcher(channel.getChannel(), response);

        ConsumerAckMessage result = (ConsumerAckMessage) request.getMsgParams();

        if (result.getStatus() == ConsumerAckMessage.SUCCESS) {
            ConsumerContext.addOrUpdateClustersState(task.getClusters(), ClustersState.SUCCESS);
        }
    }

}
