package com.atguigu.gulimall.order.listener;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * RabbitMQ订单关闭的监听器
 * @author:厚积薄发
 * @create:2022-11-08-19:46
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    OrderService orderService;

    /**
     * 订单超时，关闭订单（将 0 - 待支付状态改为 4 - 已取消状态）
     * @param entity
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("OrderCloseListener收到过期的订单信息，准备关闭订单" + entity.getOrderSn());
        try{
            orderService.closeOrder(entity);
            //手动调用支付宝收单（通知支付宝订单超时，使用户无法继续付款）
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //关闭订单失败，将消息放回消息队列中
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
