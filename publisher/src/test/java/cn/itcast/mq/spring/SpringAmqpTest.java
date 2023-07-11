package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j

public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() {
        String queueName = "simple.queue";
        String message = "hello, spring amqp!";
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @Test
    public void testSendMessage2WorkQueue() throws InterruptedException {
        String queueName = "simple.queue";
        String message = "hello, message__";
        for (int i = 1; i <= 2; i++) {
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }

    @Test
    public void testSendFanoutExchange() {
        // 交换机名称
        String exchangeName = "itcast.fanout";
        // 消息
        String message = "hello, every one!";


        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    @Test
    public void testSendDirectExchange() throws InterruptedException {
        // 交换机名称
        String exchangeName = "itcast.direct";
        // 消息
        String message = "hello, test!";
        // 发送消息
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            } else {
                log.info("消息发送失败:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message1, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message1);
        });
        rabbitTemplate.convertAndSend(exchangeName, "blue2", message);
        Thread.sleep(1000);
    }

    @Test
    public void testSendTopicExchange() {
        // 交换机名称
        String exchangeName = "topic.exchange";

        String exchangeName2 = "itcast.topic";
        // 消息
        String message = "今天天气不错，我的心情好极了!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "test.1", message);
    }

    @Test
    public void testDelayQueue() {
        // 交换机名称
        String exchangeName = "delay.exchange";
        // 消息
        String message = "hello, delay!";

        String routingKey = "delay.routingKey";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, msg -> {
            msg.getMessageProperties().setDelay(10000);
            return msg;
        });
        log.info("当前时间：{}，发送一条延迟{}毫秒的信息给队列delay.queue:{}", new Date(), 30000, message);
    }

    @Test
    public void testDeadQueue() throws InterruptedException {
        String exchangeName = "delay.exchange2";
        String routingKey = "delay.routingKey3";
        String message = "hello, dead!2";
        String ttl = "20000";
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            } else {
                log.info("消息发送失败:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message1, replyCode, replyText, exchange, routingKey1) -> {
            log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey1, replyCode, replyText, message1);
        });
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, msg -> {
            msg.getMessageProperties().setExpiration(ttl);
            msg.getMessageProperties().setMessageId("123456");
            return msg;
        });
        log.info("当前时间：{}，发送一条延迟{}毫秒的信息给队列delay.queue:{}", new Date(), 5000, message);
        Thread.sleep(1000);
    }
}
