package com.test.base.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 接收消息
 * 1.创建消费者Consumer，制定消费者组名
 * 2.指定Nameserver地址
 * 3.订阅主题Topic和Tag
 * 4.设置回调函数，处理消息
 * 5.启动消费者consumer
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-sync");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 3.订阅主题Topic和Tag
        consumer.subscribe("base", "tag1");
        // 指定消费两个类型，如果消费所有tag类型，参数为"*"
        // consumer.subscribe("base", "tag1 || tag2");
        // 指定消息处理模式，广播模式(每一个消费者都接收到消息)|负载均衡模式(只有一个消费者接收到消息，均匀分发，默认方式)
        /* consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setMessageModel(MessageModel.CLUSTERING); */
        // 4.设置回调函数，处理消息
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            System.out.println("接收的消息对象时:" + list);
            list.forEach(v -> System.out.println("接收的信息为:" + new String(v.getBody())));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
