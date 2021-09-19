package com.test.orderly.producer;

import com.test.orderly.bean.OrderStep;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.List;

public class Producer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group-order");
        // 2.指定Nameserver地址，启动NameServer，默认端口号是9876
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 集群方式写法 producer.setNamesrvAddr("192.168.0.1:9876;192.168.0.2:9876");
        // 3.启动producer
        producer.start();
        // 创建消息对象
        List<OrderStep> orderSteps = OrderStep.buildOrders();
        // 发送消息
        for (int i = 0; i < orderSteps.size(); i++) {
            /*
             * 参数一: 消息对象
             * 参数二: 消息队列的选择器
             * 参数三: 选择队列的业务标识(如: 订单编号)
             */
            Message msg = new Message("OrderTopic", "Order", "i:" + i, (orderSteps.get(i) + "").getBytes());
            /*
             * 参数一: list 消息队列集合
             * 参数二: 消息对象(实际上就是消费者传递的Message对象)
             * 参数三: 业务标识(此处就是订单编号)
             * 返回: 某一个消息队列元素
             */
            SendResult sendResult = producer.send(msg, (list, message, o) -> {
                long orderId = (long) o;
                // 订单ID一样，保证消息发送到MQ时处于同一个队列，实现同步消费消息
                long index = orderId % list.size();
                // 获取消息队列的对象并返回
                return list.get((int) index);
            }, orderSteps.get(i).getOrderId());
            System.out.println("发送结果:"+ sendResult);
        }
        producer.shutdown();
    }
}
