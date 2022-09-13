package com.atguigu.guliamll.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**oss
 * 1、引入oss-starter
 * 2、配置key、endpoint相关信息即可
 * 3、使用OSSClient 进行相关操作
 */
@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    @Resource
    OSSClient ossClient;

    @Test
    public void Upload() throws FileNotFoundException {
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
