package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @author:厚积薄发
 * @create:2022-10-28-14:43
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    MemberFeignService memberFeignService;

    //微博授权接口
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        //1、根据code换取accessToken
        //请求体map放入相应参数
//        Map<String, String> map = new HashMap<>();
//        map.put("client_id","3266648217");
//        map.put("client_secret","4b315609818c1e6066c95542c4be1bfa");
//        map.put("grant_type","authorization_code");
//        map.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
//        map.put("code",code);
//        //使用HttpUtils工具类发送请求。host:主机地址、path:路径、method:请求方式、headers:请求头、querys:查询参数、bodys:请求体
//        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<String,String>(), new HashMap<String,String>(),map);

        String url = "https://api.weibo.com/oauth2/access_token?client_id=3266648217&client_secret=4b315609818c1e6066c95542c4be1bfa&grant_type=authorization_code&redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success&code=" + code;
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = httpClient.execute(httpPost);

        System.out.println("response：" + response.getStatusLine().getStatusCode());
        //2、处理
        if (response.getStatusLine().getStatusCode() == 200) {
            //获取到了acessToken
            String json = EntityUtils.toString(response.getEntity());//apache工具类，将实体类对象转换为String
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //知道当前哪个社交账户
            //当前用户如果是第一次进入网站，自动注册进来（为当前社交用户生成一个会员信息账号，以后这个社交账号就对应指定会员）
            //todo 远程方法 登录或者注册这个社交用户
            R oauthLogin = memberFeignService.oauthLogin(socialUser);
            if (oauthLogin.getCode() == 0) {
                MemberRespVo data = oauthLogin.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功，用户：{}", data.toString());

                //第一次使用session，命令浏览器保存卡号。以后浏览器访问哪个网站会带上这个网站的cookie
                //默认发的令牌。session=asdasncizx……。作用域：当前域（解决子域session共享问题）、json序列化方式进行序列化
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);
                //登录成功跳回首页
                return "redirect:http://gulimall.com";
            }
        }

        //失败：：
        return "redirect:http://gulimall.com/login.html";
    }
}
