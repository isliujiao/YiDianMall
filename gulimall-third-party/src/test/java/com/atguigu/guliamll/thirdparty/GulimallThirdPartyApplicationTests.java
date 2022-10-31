package com.atguigu.guliamll.thirdparty;

import com.aliyun.oss.OSSClient;
import com.atguigu.guliamll.thirdparty.component.SmsComponent;
import com.atguigu.guliamll.thirdparty.util.HttpUtils;
import org.apache.http.HttpResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


/**
 * oss
 * 1、引入oss-starter
 * 2、配置key、endpoint相关信息即可
 * 3、使用OSSClient 进行相关操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Autowired
    @Resource
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSendCode(){
        smsComponent.sandSmsCode("15666007723","9999");
    }


    /**
     * 阿里云 短信发送
     */
    @Test
    public void sendSms() {
        String host = "https://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "1995167581a24a1f814d70a993fd687b";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:1234");
        bodys.put("phone_number", "15666007723");
        bodys.put("template_id", "TPL_0000");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 阿里云对象存储服务（Object Storage Service） -- OSS
      * @throws FileNotFoundException
     */
    @Test
    public void Upload () throws FileNotFoundException {
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tPn1tM4bTbM5oeEtDQW";
//        String accessKeySecret = "2v2gEVISYxiZYVfppedvBd7Pn0Ibvh";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        FileInputStream inputStream = new FileInputStream("F:\\SpringBoot-SpringCloud-SpringCloudAlibaba对照表.png");

        ossClient.putObject("gulimall-isliujiao", "SpringBoot-SpringCloud-SpringCloudAlibaba对照表.png", inputStream);

        ossClient.shutdown();
        System.out.println("上传成功");
    }


}
