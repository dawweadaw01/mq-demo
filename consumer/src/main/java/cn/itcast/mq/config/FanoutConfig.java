package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FanoutConfig {
    // itcast.fanout
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("itcast.fanout");
    }

    // fanout.queue1
    @Bean
    public Queue fanoutQueue1(){
        return new Queue("fanout.queue1");
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topic.exchange",true,false);
    }

    @Bean
    public Queue topicQueue3(){
        HashMap<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead.exchange");
        args.put("x-dead-letter-routing-key", "dead.routingKey");
        return new Queue("topic.queue3",false,false,false,args);
    }

    @Bean
    public Binding topicBinding1(Queue topicQueue3, TopicExchange topicExchange){
        return BindingBuilder
                .bind(topicQueue3)
                .to(topicExchange)
                .with("test.*");
    }

    // 绑定队列1到交换机
    @Bean
    public Binding fanoutBinding1(Queue fanoutQueue1, FanoutExchange fanoutExchange){
        return BindingBuilder
                .bind(fanoutQueue1)
                .to(fanoutExchange);
    }

    // fanout.queue2
    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanout.queue2");
    }

    // 绑定队列2到交换机
    @Bean
    public Binding fanoutBinding2(Queue fanoutQueue2, FanoutExchange fanoutExchange){
        return BindingBuilder
                .bind(fanoutQueue2)
                .to(fanoutExchange);
    }

    @Bean
    public Queue objectQueue(){
        return new Queue("object.queue");
    }

    @Bean
    public Queue delayQueue1(){
        return new Queue("delay.queue1",false,false,false,null);
    }

    @Bean
    public Queue delayQueue2(){
        return new Queue("delay.queue2",false,false,false,null);
    }

    @Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("delay.exchange", "x-delayed-message", false, false, args);
    }

    @Bean
    public Binding bindingDelayExchange1(Queue delayQueue1, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue1).to(delayExchange).with("delay.routingKey").noargs();
    }
    @Bean
    public Binding bindingDelayExchange2(Queue delayQueue2, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue2).to(delayExchange).with("delay.routingKey2").noargs();
    }

    @Bean
    public DirectExchange delayExchange2(){
        return new DirectExchange("delay.exchange2");
    }

    @Bean
    public Queue delayQueue3(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dead.exchange");
        args.put("x-dead-letter-routing-key", "dead.routingKey");
        return new Queue("delay.queue3",false,false,false,args);
    }

    @Bean
    public Binding bindingDelayExchange3(Queue delayQueue3, DirectExchange delayExchange2) {
        return BindingBuilder.bind(delayQueue3).to(delayExchange2).with("delay.routingKey3");
    }
    /*
    死信交换机
     */
    @Bean
    public DirectExchange deadExchange(){
        return new DirectExchange("dead.exchange");
    }
    @Bean
    public Queue deadQueue(){
        return new Queue("dead.queue");
    }

    @Bean
    public Binding bindingDeadExchange(Queue deadQueue, DirectExchange deadExchange) {
        return BindingBuilder.bind(deadQueue).to(deadExchange).with("dead.routingKey");
    }
}
