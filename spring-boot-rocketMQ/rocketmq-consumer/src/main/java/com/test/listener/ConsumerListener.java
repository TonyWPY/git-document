package com.test.listener;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

// topic是生产者发送的主题，consumeMode消息类型(广播和负载均衡)，consumerGroup指定group组
@Component
@RocketMQMessageListener(topic = "spring-boot-rocketmq", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "${rocketmq.consumer.group}")
public class ConsumerListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println("接收的消息为: " + s);
    }
}
