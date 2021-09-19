package com.test.orderly.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;

public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-order");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 3.订阅主题Topic和Tag
        consumer.subscribe("OrderTopic", "*");
        // 4.注册消息监听器(MessageListenerOrderly表示消息同步(顺序)接收)
        // 对于一个消息队列，采用一个线程去处理
        consumer.registerMessageListener((MessageListenerOrderly) (list, context) -> {
            list.forEach(v -> System.out.println("线程名称: ["+ Thread.currentThread().getName() +"]; 消费的消息是: " + new String(v.getBody())));
            return ConsumeOrderlyStatus.SUCCESS;
        });
        // 启动消费者
        consumer.start();
    }
}
