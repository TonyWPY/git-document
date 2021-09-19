package com.test.filter.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

public class ConsumerSql {
    public static void main(String[] args) throws Exception {
        // 1.创建消费者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group-filter-sql");
        // 2.指定Nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");
        // 3.订阅主题Topic和Tag，从消费者设置的用户属性userProperty中过滤消息
        /*
         * * 数值比较，比如：**>，>=，<，<=，BETWEEN，=；**
         * 字符比较，比如：**=，<>，IN；**
         * **IS NULL** 或者 **IS NOT NULL；**
         * 逻辑符号 **AND，OR，NOT；**

         常量支持类型为：

         * 数值，比如：**123，3.1415；**
         * 字符，比如：**'abc'，必须用单引号包裹起来；**
         * **NULL**，特殊的常量
         * 布尔值，**TRUE** 或 **FALSE**
         */
        consumer.subscribe("filter-sql", MessageSelector.bySql("i between 0 and 3"));
        // 4.设置回调函数，处理消息
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            list.forEach(v -> System.out.println("接收的信息为:" + new String(v.getBody())));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }
}
