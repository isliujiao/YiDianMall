package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Properties;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTest(){
        String msg = "Hello World!";
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        reasonEntity.setName("·我是OrderReturnReasonEntity传来的·");
        /**
         * 1、发消息组件(如果发送的消息是对象，该对象必须实现序列化Serializable)
         * 指定交换机、路由键、消息
         */
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
        log.info("消息【{}】发送完成",reasonEntity);
    }

    //创建交换机
    @Test
    public void creatExchange() {
        // 创建一个直接类型的交换机
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange【{}】创建成功","hello-java-exchange");
    }

    //创建队列
    @Test
    public void creatQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue【{}】创建成功","hello-java-queue");
    }

    //创建绑定
    @Test
    public void creatBinding(){
        /**
         * 新建绑定关系，将指定的交换机和目的地进行绑定
         * String destination 【目的地（队列 / 交换机）】
         * DestinationType destinationType【目的地类型（队列 / 交换机）】
         * String exchange【交换机】
         * String routingKey【路由键】
         * Map<String, Object> arguments) 【自定义参数】
         */
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding【{}】创建成功","hello-java-binding");
    }
}
