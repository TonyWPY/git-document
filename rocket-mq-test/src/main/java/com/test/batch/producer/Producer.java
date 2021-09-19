package com.test.batch.producer;

import com.test.batch.split.ListSplitter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;

public class Producer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group-batch");
        // 2.指定Nameserver地址，启动NameServer，默认端口号是9876
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 集群方式写法 producer.setNamesrvAddr("192.168.0.1:9876;192.168.0.2:9876");
        // 3.启动producer
        producer.start();
        // 批量发送消息(最大不能超过4M)
        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message("batch", "tag", ("hello-rocketmq-batch-" + 1).getBytes()));
        messageList.add(new Message("batch", "tag", ("hello-rocketmq-batch-" + 2).getBytes()));
        messageList.add(new Message("batch", "tag", ("hello-rocketmq-batch-" + 3).getBytes()));
        // 消息的限制处理
        // 把大的消息分裂成若干个小的消息
        ListSplitter splitter = new ListSplitter(messageList);
        while (splitter.hasNext()) {
            try {
                List<Message> listItem = splitter.next();
                SendResult result = producer.send(listItem);
                // 获得消息的状态
                SendStatus sendStatus = result.getSendStatus();
                // 获得消息的ID
                String msgId = result.getMsgId();
                // 获得消息的队列ID
                int queueId = result.getMessageQueue().getQueueId();
                System.out.println("消息的状态:"+sendStatus+";消息的ID:"+msgId+";队列ID:"+queueId);
                producer.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
                //处理error
            }
        }
    }
}
