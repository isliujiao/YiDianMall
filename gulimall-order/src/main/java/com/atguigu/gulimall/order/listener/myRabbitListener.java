package com.atguigu.gulimall.order.listener;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RabbitMQ测试
 * @author:厚积薄发
 * @create:2022-11-02-14:33
 */
@RabbitListener(queues = {"hello-java-queue"})
@Component
public class myRabbitListener {

    /**
     * queues:声明需要监听的所有队列
     * class org.springframework.amqp.core.Message
     *
     * 参数可以写以下类型：
     * 1、Message message：原生消息详细信息。头+体
     * 2、T <发送消息的类型>：指定 OrderReturnReasonEntity content
     * 3、Channel channel：当前传输数据的通道
     *
     * Queue：可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个队列收到消息
     * 场景：
     *      1）、订单服务启动多个：同一个消息，只能有一个客户端收到
     *      2）、只有一个消息完全处理完，方法运行结束才可以接收到下一个消息
     */
    @RabbitHandler
    public void receiveMessage(Message message, OrderReturnReasonEntity content, Channel channel) {
        byte[] body = message.getBody();//消息体
        MessageProperties messageProperties = message.getMessageProperties();//消息头
        System.out.println("接收到消息…:" + message + "内容===》" + content);

        //channel通道内按顺序自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if(deliveryTag % 2 == 0){
                //手动确认消息，multiple:false(非批量模式)
                channel.basicAck(deliveryTag,false);
                System.out.println("消费了消息： " + deliveryTag);
            }else {
                //拒绝消费消息
                //(long deliveryTag, boolean multiple[是否批量操作], boolean requeuep[true:重新入队，false:丢弃])
                channel.basicNack(deliveryTag,false,true);
                //long deliveryTag, boolean requeue
//                channel.basicReject();
            }
        } catch (IOException e) {
            //网络中断、通断中断
        }
    }

    @RabbitHandler
    public void recieveMessage2(OrderEntity content) throws InterruptedException {
        //{"id":1,"name":"哈哈","sort":null,"status":null,"createTime":1581144531744}
        System.out.println("接收到消息..."+content);
    }

}
