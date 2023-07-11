package cn.itcast.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

/**
 * @author banyanmei
 */
@Component
@Slf4j
public class SpringRabbitListener {

    // @RabbitListener(queues = "simple.queue")
    // public void listenSimpleQueue(String msg) {
    //     System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");
    // }

    private static final int MAX_RETRIES = 3;

    private static final long RETRY_INTERVAL = 2;

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue1(Message msg) throws InterruptedException {
        System.out.println("消费者1接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue2(Message msg) throws InterruptedException {
        System.err.println("消费者2........接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String msg) {
        System.out.println("消费者接收到fanout.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String msg) {
        System.out.println("消费者接收到fanout.queue2的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
    ))
    public void listenDirectQueue1(String msg) {
        System.out.println("消费者接收到direct.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}
    ))
    public void listenDirectQueue2(String msg) {
        System.out.println("消费者接收到direct.queue2的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void listenTopicQueue1(String msg) {
        System.out.println("22消费者接收到topic.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))

    @RabbitListener(queues = "topic.queue3")
    public void listenTopicQueue3(Message msg, Channel channel) throws InterruptedException, IOException {
        System.out.println("消费者接收到topic.queue3的消息：【" + msg + "】");
//        int idx = 0;
//        while (idx < MAX_RETRIES) {
//            idx++;
//            String errorTip = "第" + idx + "次消费失败" +
//                    ((idx < 3) ? "," + RETRY_INTERVAL + "s后重试" : ",进入死信队列");
//            log.error(errorTip);
//            Thread.sleep(RETRY_INTERVAL * 1000);
//        }
//       channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false);
//        log.info("消息已经拒绝,进入死信队列");
        //抛出异常
        throw new RuntimeException("消息消费失败");
    }
    public void listenTopicQueue2(String msg) {
        System.out.println("消费者接收到topic.queue2的消息：【" + msg + "】");
    }

    @RabbitListener(queues = "object.queue")
    public void listenObjectQueue(Map<String, Object> msg) {
        System.out.println("接收到object.queue的消息：" + msg);
    }

    @RabbitListener(queues = "delay.queue1")
    public void listenDelayQueue1(String msg) {
        log.info("当前时间：{}，收到一条延迟毫秒的信息给队列delay1.queue:{}", new Date(), msg);
    }

    @RabbitListener(queues = "delay.queue2")
    public void listenDelayQueue2(String msg) {
        log.info("当前时间：{}，收到一条延迟毫秒的信息给队列delay2.queue:{}", new Date(), msg);
    }

    @RabbitListener(queues = "topic.queue1")
    public void listenTopicQueue1(Message msg) {
        System.out.println("消费者接收到topic.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(queues = "dead.queue", concurrency = "10")
    public void listenDeadQueue(Message msg) {
        log.info("当前时间：{}，收到一条延迟毫秒的信息给队列dead.queue:{}", new Date(), msg);
    }

}
