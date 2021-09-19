package com.test.base.producer;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 发送异步消息
 * 1.创建消息生产者producer，并制定生产者组名
 * 2.指定Nameserver地址
 * 3.启动producer
 * 4.创建消息对象，指定主题Topic、Tag和消息体
 * 5.发送消息
 * 6.关闭生产者producer
 */
public class AsyncProducer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group-async");
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
            Message message = new Message("base", "tag2", ("hello-rocketmq-" + i).getBytes());
            // 发送异步消息，通过回调函数拿到异步返回值
            producer.send(message, new SendCallback() {
                // 发送成功回调函数
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送的结果: " + sendResult);
                }
                // 发送失败回调函数
                @Override
                public void onException(Throwable throwable) {
                    System.out.println("发送异常: " + throwable.getMessage());
                }
            });
            // 线程休息一秒再发送第二个消息
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }
}
