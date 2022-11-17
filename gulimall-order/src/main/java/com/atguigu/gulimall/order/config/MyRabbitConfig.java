package com.atguigu.gulimall.order.config;

import com.baomidou.mybatisplus.annotation.TableField;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * @author:厚积薄发
 * @create:2022-11-02-11:20
 */
@Configuration
public class MyRabbitConfig {

//    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 解决循环依赖错误
     * The dependencies of some of the beans in the application context form a cycle:
     *
     *    servletEndpointRegistrar defined in class path resource [org/springframework/boot/actuate/autoconfigure/endpoint/web/ServletEndpointManagementContextConfiguration$WebMvcServletEndpointManagementContextConfiguration.class]
     *       ↓
     *    healthEndpoint defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]
     *       ↓
     *    healthIndicatorRegistry defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthIndicatorAutoConfiguration.class]
     *       ↓
     *    org.springframework.boot.actuate.autoconfigure.amqp.RabbitHealthIndicatorAutoConfiguration
     * ┌─────┐
     * |  rabbitTemplate defined in class path resource [org/springframework/boot/autoconfigure/amqp/RabbitAutoConfiguration$RabbitTemplateConfiguration.class]
     * ↑     ↓
     * |  myRabbitConfig (field org.springframework.amqp.rabbit.core.RabbitTemplate com.atguigu.gulimall.order.config.MyRabbitConfig.rabbitTemplate)
     * └─────┘
     */
    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }
    /**
     * RabbitMQ发送消息序列化转为JSON格式
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自己定制RabbitTemplate
     * 1、 服务器收到消息就回调（发送端 --> Broker）
     *       1）、spring.rabbitmq.publisher-confirms=true
     *       2）、rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback()
     * 2、消息正确抵达队列进行回调（Broker --> Queue）
     *      1）、spring.rabbitmq.publisher-returns=true
     *           spring.rabbitmq.template.mandatory=true
     *      2）、设置确认回调ReturnCallback、
     * 3、消费端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）
     *      spring.rabbitmq.listener.simple.acknowledge-mode=manual：手动签收
     *      1）、默认是自动确认的，只要消息接收到，客户端自动确认，服务端就会移除这个消息
     *          问题：收到很多消息，自动回复给服务ack，只有一个消息处理成功之后宕机。发生消息丢失。
     *          解决：手动确认。只要我们没有明确告诉MQ消息被消费，即没有Ack，消息就一直是unacked状态。即使Consumer宕机，
     *                消息也不会丢失，而是重新变为Ready。下一次有新的Consumer连接进来就重新发给他
     *      2)、如何签收：
     *             channel.basicAck(deliveryTag,false);消费消息。业务成功后
     *             channel.basicNack(deliveryTag,false,true);//拒绝消费。业务失败-
     *
     * @PostConstruct：MyRabbitConfig对象创建完成以后，执行这个方法
     */
//    @PostConstruct
    public void initRabbitTemplate() {
        //设置发送端确认回调 （发送端 --> Broker）
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 1、只要消息抵达Broker --> ack=true
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param ack             消息是否成功收到
             * @param cause           失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm...correlationData=["+correlationData+",  ack=["+ack+"],  cause=["+cause+"]");
            }
        });

        //如果失败，返回消息抵达队列的确认回调 （Broker --> Queue）
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只有消息没有投递给指定的队列，就触发这个·失败·回调
             * @param message 投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange 当时这个消息发给哪个交换机
             * @param routingKey 当时这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail Message=["+message+"],  replyCode=["+replyCode+"],  replyText=["+replyText+"],  exchange=["+exchange+"],  routingKey=["+routingKey+"]");
            }
        });
    }
}
