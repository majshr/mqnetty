package com.mq.broker.disparch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;

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
 * 发送消息任务
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月27日 下午5:05:17
 */
public class SendMessageTask implements Runnable {
    private MessageDispatchTask[] tasks;
    private Phaser phaser = null;
    private SendMessageLauncher launcher = SendMessageLauncher.getInstance();

    public SendMessageTask(Phaser phaser, MessageDispatchTask[] tasks) {
        this.phaser = phaser;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        for (MessageDispatchTask task : tasks) {
            Message message = task.getMessage();
            if (ConsumerContext.getClustersState(task.getClusters()) != null) {
                RemoteChannelData channel = ConsumerContext.selectByClusters(task.getClusters())
                        .nextRemoteChannelData();

                // broker发送响应信息；向消费者发送信息
                ResponseMessage response = new ResponseMessage();
                response.setMsgSource(MessageSource.MQBroker);
                response.setMsgType(MessageType.MQMessage);
                response.setMsgParams(message);
                response.setMsgId(MessageIdGenerator.UUID.generate());

                while (!NettyUtil.validateChannel(channel.getChannel())) {
                    ConsumerContext.addOrUpdateClustersState(task.getClusters(), ClustersState.NETWORKERR);
                    // 重新获取一个channel
                    channel = ConsumerContext.selectByClusters(task.getClusters())
                            .nextRemoteChannelData();
                }

                // 发送给消费者消息，获得消费者响应
                RequestMessage request = (RequestMessage) launcher.launcher(channel.getChannel(), response);

                ConsumerAckMessage result = (ConsumerAckMessage) request.getMsgParams();

                if (result.getStatus() == ConsumerAckMessage.SUCCESS) {
                    ConsumerContext.addOrUpdateClustersState(task.getClusters(), ClustersState.SUCCESS);
                }
            }

            // 类似于CyclicBarrier
            phaser.arriveAndAwaitAdvance();
        }
    }

    public static void main(String[] args) {
        class TourismRunnable implements Runnable {
            Phaser phaser;
            Random random;

            public TourismRunnable(Phaser phaser) {
                this.phaser = phaser;
                this.random = new Random();
            }

            @Override
            public void run() {
                tourism();
            }

            /**
             * 旅游过程
             */
            private void tourism() {
                goToStartingPoint();
                goToHotel();
                goToTourismPoint1();
                goToTourismPoint2();
                goToTourismPoint3();
                goToEndPoint();
            }

            /**
             * 装备返程
             */
            private void goToEndPoint() {
                goToPoint("飞机场,准备登机回家");
            }

            /**
             * 到达旅游点3
             */
            private void goToTourismPoint3() {
                goToPoint("旅游点3");
            }

            /**
             * 到达旅游点2
             */
            private void goToTourismPoint2() {
                goToPoint("旅游点2");
            }

            /**
             * 到达旅游点1
             */
            private void goToTourismPoint1() {
                goToPoint("旅游点1");
            }

            /**
             * 入住酒店
             */
            private void goToHotel() {
                goToPoint("酒店");
            }

            /**
             * 出发点集合
             */
            private void goToStartingPoint() {
                goToPoint("出发点");
            }

            private int getRandomTime() {
                int time = this.random.nextInt(400) + 100;
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return time;
            }

            private void goToPoint(String point) {
                try {
                    String name = Thread.currentThread().getName();
                    System.out.println(name + " 花了 " + getRandomTime() + " 时间才到了" + point);
                    // 和CyclicBarrier用法相同；所有线程都走到这一步后，同时继续向后执行
                    phaser.arriveAndAwaitAdvance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String name = "明刚红丽黑白";
        Phaser phaser = new Phaser(name.length());
        List<Thread> tourismThread = new ArrayList<>();
        for (char ch : name.toCharArray()) {
            tourismThread.add(new Thread(new TourismRunnable(phaser), "小" + ch));
        }
        for (Thread thread : tourismThread) {
            thread.start();
        }

        /*
         * CountDownLatch主要使用的有2个方法
         * await()方法，可以使线程进入等待状态，在Phaser中，与之对应的方法是awaitAdvance(int n)。
         * countDown()，使计数器减一，当计数器为0时所有等待的线程开始执行，在Phaser中，与之对应的方法是arrive()
         * 
         * 
         * 
         * Phaser替代CyclicBarrier比较简单，CyclicBarrier的await()
         * 方法可以直接用Phaser的arriveAndAwaitAdvance()方法替代
         * CyclicBarrier与Phaser:CyclicBarrier只适用于固定数量的参与者,而Phaser适用于可变数目的屏障.
         */
    }
}
