package com.test.filter.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

public class ConsumerTag {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-filter-tag");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 3.订阅主题Topic和Tag，不过滤filter-tag组下的所有tag消息
        consumer.subscribe("filter-tag", "*");
        // 过滤filter-tag组下的所有非tag和tag1消息
        // consumer.subscribe("filter-tag", "tag || tag1");
        // 4.设置回调函数，处理消息
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            list.forEach(v -> System.out.println("接收的信息为:" + new String(v.getBody())));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
