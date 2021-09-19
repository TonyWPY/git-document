package com.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RocketProducerApplication.class })
public class RocketProducerTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void test() {
        // 该方法发送简单的消息
        // 第一个参数是主题，第二个参数指定消息内容
        this.rocketMQTemplate.convertAndSend("spring-boot-rocketmq", "hello rocketmq");
        log.info("消息发送成功");
    }
}
