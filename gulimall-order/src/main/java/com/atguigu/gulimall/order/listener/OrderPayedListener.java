package com.atguigu.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author:厚积薄发
 * @create:2022-11-10-20:41
 */
@RestController
public class OrderPayedListener {

    /**
     * 内网穿透，使支付宝访问以下请求方式：
     */
    /**
     * 1.Nginx
     * server {
     *     listen       80;
     *     server_name gulimall.com *.gulimall.com gulimallorder.free.idcfengye.com;
     *
     *     #charset koi8-r;
     *     #access_log  /var/log/nginx/log/host.access.log  main;
     *
     *     #由nginx处理静态资源，除了static文件外 交由下面 / 处理
     *     location /static/{
     *       root /usr/share/nginx/html;
     *     }
     *
     *     location /payed/ {
     *       proxy_set_header Host order.gulimall.com;
     *       proxy_pass http://gulimall;
     *     }
     *     location / {
     *       proxy_set_header Host $host;
     *       proxy_pass http://gulimall;
     *     }
     */
    /**
     * 2.使用https://www.ngrok.cc/
     *  设置本地端口：order.gulimall.com:80
     *  使用客户端建立内网穿透隧道
     */

    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    /**
     * @param request
     * @return
     */
    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo vo,HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
        //只要我们收到了支付宝给我们异步的通知，告诉我们订单成功返回success，支付宝就停止通知

        //验签
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
        if(signVerified){
            //签名验证成功
            System.out.println("签名验证成功");
            String result = orderService.handlePayResult(vo);
            return result;
        }else{
            System.out.println("签名验证失败");
            return "error";
        }



    }



    //自己测试的内网穿透访问
    @GetMapping("/payed/gulimall")
    public String myGuliMall(Model model) {
//        return "<a href='http://localhost:10001'>000</a>";
        return "<a href='http://www.baidu.com'>000</a>";
    }

}
