package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author:厚积薄发
 * @create:2022-10-26-9:10
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    //发送短信验证码
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {

        //1、接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {//如果redis存在验证码，则不能发送验证码
            //取用_分割的第二个数值（时间）
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //60秒内不能再发送
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //2、验证码再次校验。redis。存key-phone，value-code
        String code = UUID.randomUUID().toString().substring(0, 5);
        String redisTimeCode = code + "_" + System.currentTimeMillis();

        //redis缓存验证码，防止同一个phone在60秒内再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, redisTimeCode, 10, TimeUnit.MINUTES);
        System.out.println("code：" + code);

        //todo 调用远程方法发送验证码
        thirdPartFeignService.sendCode(phone, code);

        return R.ok();
    }

    /**
     * 注册
     * //todo 重定向携带数据，利用session原理，将数据反正该session中，
     * 只要跳到下一个页面取出这个数据以后，session里面的数据就会删掉
     *
     * @param vo
     * @param result
     * @param redirectAttributes 重定向视图并携带数据
     * @return
     */
    @PostMapping("/register")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        //前置校验,如果出错返回注册页面并携带错误信息
        if (result.hasErrors()) {
            //getField作为key，getDefaultMessage作为value
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

//            model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors", errors);

            //校验错误，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //注册.调用远程服务进行注册
        //1、校验验证码
        String code = vo.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

        if (!StringUtils.isEmpty(redisCode) && code.equals(redisCode.split("_")[0])) {//判断redis中验证码是否为空、用户输入验证码和redis是否一致
            //删除验证码;令牌机制（防止注册完成后使用相同手机号验证码进行多次注册）
            redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
            //TODO 验证码通过，调用远程服务进行注册
            R r = memberFeignService.regist(vo);
            if (r.getCode() == 0) {
                //成功
                return "redirect:http://auth.gulimall.com/login.html";
            } else {
                //失败
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", r.getData("msg",new TypeReference<String>(){}));
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }

        } else {//验证码错误
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //验证码校验错误，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }

    //页面登录功能（接收k-v键值对数据）
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){

        //todo 远程调用实现登录
        R login = memberFeignService.login(vo);
        if(login.getCode() == 0){
            //成功(存储Session)
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            System.out.println("data：" + data);
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://gulimall.com";
        }

        //失败
        Map<String,String> errors = new HashMap<>();
        errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
        redirectAttributes.addFlashAttribute("errors",errors);
        return "redirect:http://auth.gulimall.com/login.html";

    }

    //登录跳转请求
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            //没登录
            return "login";
        }
        return  "redirect:http://gulimall.com";
    }


}
