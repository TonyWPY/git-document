package com.test.transaction.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.concurrent.TimeUnit;

/**
 * 1）事务消息发送及提交
 * 发送消息（half消息）
 * 服务端响应消息写入结果
 * 根据发送结果执行本地事务（如果写入失败，此时half消息对业务不可见，本地逻辑不执行）
 * 根据本地事务状态执行Commit或者Rollback（Commit操作生成消息索引，消息对消费者可见）
 *
 * 2）事务补偿
 * 对没有Commit/Rollback的事务消息（pending状态的消息），从服务端发起一次“回查”
 * Producer收到回查消息，检查回查消息对应的本地事务的状态
 * 根据本地事务状态，重新Commit或者Rollback
 * 其中，补偿阶段用于解决消息Commit或者Rollback发生超时或者失败的情况。
 *
 * 3）事务消息状态
 * 事务消息共有三种状态，提交状态、回滚状态、中间状态：
 * TransactionStatus.CommitTransaction: 提交事务，它允许消费者消费此消息。
 * TransactionStatus.RollbackTransaction: 回滚事务，它代表该消息将被删除，不允许被消费。
 * TransactionStatus.Unknown: 中间状态，它代表需要检查消息队列来确定状态。
 */

public class Producer {
    public static void main(String[] args) throws Exception {
        // 1.创建消息生产者producer，并制定生产者组名
        TransactionMQProducer producer = new TransactionMQProducer("group-transaction");
        // 2.指定Nameserver地址，启动NameServer，默认端口号是9876
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 集群方式写法 producer.setNamesrvAddr("192.168.0.1:9876;192.168.0.2:9876");
        // 3. 创建消息事件的监听器，即生产者的事物实行成功与否要告知MQ
        producer.setTransactionListener(new TransactionListener() {
            // 执行本地事物
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                if (StringUtils.equals("tag-1", message.getTags())) {
                    // 消息事物提交，MQ执行消息的存储，发送给消费方
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StringUtils.equals("tag-2", message.getTags())) {
                    // 消息事物回滚，MQ执行消息的销毁，不发送给消费方
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                } else if (StringUtils.equals("tag-3", message.getTags())) {
                    // 消息状态未知，MQ回查生产方的事物状态，回调checkLocalTransaction方法
                    return LocalTransactionState.UNKNOW;
                }
                return LocalTransactionState.UNKNOW;
            }

            // MQ回查消息事物的状态，MQ回调本地的事物进行排查
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // tag-3将会执行此方法
                System.out.println("当前回查消息的tag:" + messageExt.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        // 3.启动producer
        producer.start();

        String[] tags = { "tag-1", "tag-2", "tag-3" };

        // 发送3个消息
        for (int i = 0; i < tags.length; i++) {
            // 4.创建消息对象，指定主题Topic、Tag和消息体
            /*
             * 参数说明
             * Topic 消息主题(消息类别)
             * Tag 消息主题下的标签(消息主题下的类别)
             * content 消息内容
             */
            Message message = new Message("transaction", tags[i], ("hello-rocketmq-transaction-" + i).getBytes());
            // 发送半消息，第二个参数指定:事物控制应用到某一个消息上或者整个producer，null表示对整个producer进行事物控制
            SendResult result = producer.sendMessageInTransaction(message, null);
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
        // 此处不要关闭，否则执行不到回调的方法
        // producer.shutdown();
    }
}
