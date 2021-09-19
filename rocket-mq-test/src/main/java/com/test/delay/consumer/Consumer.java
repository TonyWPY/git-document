package com.test.delay.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-delay");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 3.订阅主题Topic和Tag
        consumer.subscribe("delay", "tag");
        // 4.设置回调函数，处理消息
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            list.forEach(v -> System.out.println("接收的消息ID: [" + v.getMsgId() + "]; 延时时间: " + (System.currentTimeMillis() - v.getBornTimestamp())));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
