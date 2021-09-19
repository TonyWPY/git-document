package com.test.delay.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group-delay");
        // 2.指定Nameserver地址，启动NameServer，默认端口号是9876
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 集群方式写法 producer.setNamesrvAddr("192.168.0.1:9876;192.168.0.2:9876");
        // 3.启动producer
        producer.start();
        // 发送5个消息
        for (int i = 0; i < 5; i++) {
            // 4.创建消息对象，指定主题Topic、Tag和消息体
            /*
             * 参数说明
             * Topic 消息主题(消息类别)
             * Tag 消息主题下的标签(消息主题下的类别)
             * content 消息内容
             */
            Message message = new Message("delay", "tag", ("hello-rocketmq-delay-" + i).getBytes());
            // 设定发送的延迟时间 "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h" 1s~2h等级对应1~18
            message.setDelayTimeLevel(2);
            // 发送同步消息
            SendResult result = producer.send(message);
            // 获得消息的状态
            SendStatus sendStatus = result.getSendStatus();
            // 获得消息的ID
            String msgId = result.getMsgId();
            // 获得消息的队列ID
            int queueId = result.getMessageQueue().getQueueId();
            System.out.println("消息的状态:"+sendStatus+";消息的ID:"+msgId+";队列ID:"+queueId);
            // 线程休息一秒再发送第二个消息
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }
}
