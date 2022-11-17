package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkusVo;
import com.atguigu.gulimall.seckill.vo.SeckillSkuVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author:厚积薄发
 * @create:2022-11-11-19:26
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    //秒杀活动redis缓存前缀
    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    //秒杀活动商品redis缓存前缀
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    //商品信号量
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";//+商品随机码

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 商品进行秒杀(秒杀开始)
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {

        long killStart = System.currentTimeMillis();
        //获取当前用户的信息
        MemberRespVo user = LoginUserInterceptor.loginUser.get();

        //1、获取当前秒杀商品的详细信息从Redis中获取
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String skuInfoValue = hashOps.get(killId);
        if (StringUtils.isEmpty(skuInfoValue)) {
            return null;
        }
        //合法性效验
        SeckillSkuRedisTo redisTo = JSON.parseObject(skuInfoValue, SeckillSkuRedisTo.class);
        Long startTime = redisTo.getStartTime();
        Long endTime = redisTo.getEndTime();
        long currentTime = System.currentTimeMillis();
        //判断当前这个秒杀请求是否在活动时间区间内(效验时间的合法性)
        if (currentTime >= startTime && currentTime <= endTime) {

            //2、效验随机码和商品id
            String randomCode = redisTo.getRandomCode();
            String skuId = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();
            if (randomCode.equals(key) && killId.equals(skuId)) {
                //3、验证购物数量是否合理和库存量是否充足
                Integer seckillLimit = redisTo.getSeckillLimit();

                //获取信号量
                String seckillCount = redisTemplate.opsForValue().get(SKU_STOCK_SEMAPHORE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                //判断信号量是否大于0,并且买的数量不能超过库存
                if (count > 0 && num <= seckillLimit && count > num) {
                    //4、验证这个人是否已经买过了（幂等性处理）,如果秒杀成功，就去占位。userId-sessionId-skuId
                    //SETNX 原子性处理
                    String redisKey = user.getId() + "_" + skuId;
                    //设置自动过期(活动结束时间-当前时间)
                    Long ttl = endTime - currentTime;
                    Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (aBoolean) {
                        //占位成功说明从来没有买过,分布式锁(获取信号量-1)
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                        //TODO 秒杀成功，快速下单
                        boolean semaphoreCount = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        //保证Redis中还有商品库存
                        if (semaphoreCount) {
                            //创建订单号和订单信息发送给MQ
                            // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                            String timeId = IdWorker.getTimeId();
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(user.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                            orderTo.setSkuId(redisTo.getSkuId());
                            orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                            long killEnd = System.currentTimeMillis();
                            System.out.println("秒杀kill耗时：" + (killEnd - killStart));
                            return timeId;
                        }
                    }
                }
            }
        }
        long s3 = System.currentTimeMillis();
//        log.info("耗时..." + (s3 - s1));
        return null;
    }

    //返回当前时间可以参与秒杀商品信息
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        //1、确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        for (String key : keys) {
            //seckill:sessions:xxxx0000_xxxxx0000
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            //当前场次日期
            if (time >= start && time <= end) {
                //2、获取这个秒杀场次需要的所有商品信息(range:指定区间,-100 - 100:全部)
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                //批量取
                List<String> list = hashOps.multiGet(range);
                if (list != null) {
                    List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                        SeckillSkuRedisTo redisTo = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
                        return redisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }

    /**
     *  查询商品是否参加秒杀活动
     *
     * blockHandler:函数会在原方法被限流/降级/系统保护的时候调用，而fallback函数会针对所有类型的异常
     * @param skuId
     * @return
     */
    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        //自定义受保护的资源（捕捉异常）
        try (Entry entry = SphU.entry("seckillSkus")) {
            //获取所有商品的hash key
            Set<String> keys = ops.keys();
            for (String key : keys) {
                //通过正则表达式匹配 数字-当前skuid的商品
                if (Pattern.matches("\\d_" + skuId, key)) {
                    String v = ops.get(key);
                    SeckillSkuRedisTo redisTo = JSON.parseObject(v, SeckillSkuRedisTo.class);
                    //当前商品参与秒杀活动
                    if (redisTo != null) {
                        long current = System.currentTimeMillis();
                        //当前活动在有效期，暴露商品随机码返回
                        if (redisTo.getStartTime() < current && redisTo.getEndTime() > current) {
                            System.out.println("秒杀开始时间：" + sdf.format(redisTo.getStartTime()));
                            System.out.println("秒杀结束时间：" + sdf.format(redisTo.getEndTime()));
                            return redisTo;
                        } else {
                            //当前商品不再秒杀有效期，则隐藏秒杀所需的商品随机码
                            redisTo.setRandomCode(null);
                            System.out.println(redisTo.getSkuId() + "号商品不在秒杀有效期————————");
                            return redisTo;
                        }
                    }
                }
            }
        } catch (BlockException e) {

        }
        return null;
    }

    //上架最近三天参加秒杀活动的商品
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //todo 1、远程查询最近三天需要参加秒杀的活动
        R session = couponFeignService.getLates3DaySession();
        if (session.getCode() == 0 && session.size() > 0) {
            //上架商品
            List<SeckillSessionsWithSkusVo> sessionData = session.getData("data", new TypeReference<List<SeckillSessionsWithSkusVo>>() {
            });
            //缓存到redis
            //1、缓存活动信息
            saveSessionInfos(sessionData);
            //2、缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);
        }
    }

    //1、缓存秒杀活动信息
    private void saveSessionInfos(List<SeckillSessionsWithSkusVo> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                Long startTime = session.getStartTime().getTime();
                Long endTime = session.getEndTime().getTime();
                String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey) {//如果没有再执行添加操作
                    List<String> collect = session.getRelationSkus().stream().map(item ->
                            item.getPromotionSessionId() + "_" + item.getSkuId().toString()
                    ).collect(Collectors.toList());
                    redisTemplate.opsForList().leftPushAll(key, collect);
                }
            });
        } else {
            System.out.println("-----------------上架失败,最近三天没有可上架的商品-----------------");
        }
    }

    //2、缓存活动的关联商品信息
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkusVo> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                //准备hash操作
                BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                    //4、商品的随机码（防止知道接口地址压测反复抢购商品）
                    String token = UUID.randomUUID().toString().replace("-", "");
                    //活动场次场次-PromotionSessionId _ 商品属性-SkuId(1_8)
                    String sessIdAndSkuId = seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString();

                    if (!ops.hasKey(sessIdAndSkuId)) {
                        //缓存商品
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        //1、sku基本数据 todo 远程查询商品基本信息
                        R skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                        if (skuInfo.getCode() == 0) {
                            SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                            });
                            redisTo.setSkuInfo(info);
                        }

                        //2、sku秒杀信息
                        BeanUtils.copyProperties(seckillSkuVo, redisTo);

                        //3、设置当前商品的秒杀时间
                        redisTo.setStartTime(session.getStartTime().getTime());
                        redisTo.setEndTime(session.getEndTime().getTime());

                        //设置商品随机码
                        redisTo.setRandomCode(token);

                        String jsonString = JSON.toJSONString(redisTo);
                        ops.put(sessIdAndSkuId, jsonString);

                        //5、使用库存作为分布式锁的信号量（秒杀时不用直接操作数据库，而是根据库存数量进行redission分布式锁的信号量，进行限制）
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                    }
                });
            });
        }

    }
}
