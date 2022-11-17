package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 抽取阿里支付的配置文件
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000121684603";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDz2peRbrthgTDzY6ltpwHHGu7/LR35dsJbcCpt5pZ8Uoj9qFIh/hom4hePP1BASCzEw91eaf/K3v2nTCQ4IJUSJo4Bt3V3Os3xOFVhkwQ2GoZqkdDb1O08mg34iHZoE/n+PEMFqTu0FT5F68e4bKigSY/A5bziZwP3buWU5N4wHu/EpwVyQ+AjPzBM7x8t9U62XmAKRbJq7nYv/LyAucEMZ5w02JIVivv7UX8Rxtgk0gu4TkTytkUQv5B6I+sccMh0RF1pstJR/jbnQwuDOehK1wNe/9IldtTyaQ6sgxo6GoY1z9TVw1vWLms2xGsnj3SuWvnzSTiKT4HrlCaB5gPbAgMBAAECggEBALqeoH5JWxfs0fSCYgir1f11TFB3S8g9rdJijeYboTE1zOcrKOgg2gzF3LfhAV6gnbpcFw8JWhv8+exNpi5aKRuB7A2+RCRiGXew4A7Tf1aUiCgIvelvE7vcPjLk5JEUMK7zmWs8r5dK5qSGJAutBIPeqCg86YfuxKdY9FQ1ryDtHaHahrTNVfo5S269WnELakbPXxu4fHUr4B1ETNGu4YNR2pHpd0HyB9Aw+jD2QG5oy2AHDYxiazYfC8tD2a/nuolo45CZdmqrKDyQ8dbDsmSlyWLFNVLFmGU42adKmq06NxZPHMLdRwAcNsEwX/5ksJjV/fxcHCTIP6jA/g5VGtkCgYEA/eASg+rtzK7lTCYvlDtg2PDwrajI4RVnSX9I4SkgGICoQUUR/Lvil8bIu4/p/Xo05RIZwuhRhlAybEe4YccOBTAYmSXjsZTXi2ERXOEyUUribnJ3ejXehDcH52QybCHeSN45bOQxPYtS5jMQXfKgtEmzGpNgMXTiQ0YmN1OIoScCgYEA9eUMgzTNLZCKRTZTxwBU0H3f9ZZIOnyrlhiLgLPJzhn1S/ZQYUfJXZq4jqiwG+Ota0k4WoRIDS9u4622mUZu/Clu4GLrPdo4tWrhdS6KMIq7YQmC1erVTJk3S3MwXZzkt3ji2yUgvB3fkBK6t3pq2sOHeN7qwWL/fhDK30O+0C0CgYEAvbnD/IQIPOwDfloXOo75fIbrRsyZeuS08M9H1sUWMFmN5GFK5PZAi67J+qm9c2ntt7dEO03FBzxNwnAC0kehKa1c5K7VL7QNNSNQh3ngUbJF78ZplZJGfcZmiL1vlTHmXcx9W+xZBCZnyJMmyx70jGd7iwPSYoN4SFBXZQdvVwcCgYEAs3VFGoTwsa5pm3W40uXFAJy9VOknMbhHKjYjqfyM7eQ4CAZuR3Ey3yPKSiOG0/PoXwKDJFyikSzrCzN+roKfAV3j09Odx/h5pr2oafNbW24Erx9X43ON1GeF2YTp6YhVzClnt3RzXZeXz/g7WSj4z0wglPBr+FOeD42F4kf4/MkCgYAhblAXkAp3DpobPg4bCw02l29RgWhwpCvbYOJtgbP5/QgfM7C+zO1Spgs9KUyypUqLAkLsz2+Bao3eGg0kMb033IzOhhNyjYwxWJEl2V+Ws//ogWCRj1CFtJlxyASl16E5MXZ287R4QSC6xKa2HeBM7km4WhAWO0o8DL0BXRza+w==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxMRpuFHDE0uc9VdCK1SwYBlWfOJv7cv//eBDX8gjXFwSfBNnPSjcCO3EXgmYwuqRbLJ06vY2tPyvHYeDZ6kVunERDGhpxyFZtB0yE3aiBLAM/vJhLmcD7ofvmzNlo2CyRlyVRQImYf47nMKD2zwnWEjgeKLSkr0Fqr8KVa/8zvLkUTTi9Dt62w8+xg3+S6mtm1ovzMKMvMHjKq2FrZBshSIl0ulFEfte3eC6ppSEMCwHXm1qFlm7ExIXIxY/Ftdgdb4FjRIcPzBPBs/lvJQLsTzfclJ+ONmcplrR7Boiywwy4n735S6tSFyUnM9MrMU8hITKMpcGX0kFFKZWXz2RfwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://gulimallpro.free.idcfengye.com/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
//    private  String return_url = "http://gulimallpro.free.idcfengye.com/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
