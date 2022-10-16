package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author:厚积薄发
 * @create:2022-09-28-19:58
 */
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    //一级分类
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        //todo 1、查出一级分类
        List<CategoryEntity> categoryEntitys = categoryService.getLevel1Categorys();

        //视图解析器进行拼串
        // classpath:/templates/ + 返回值 + .html
        model.addAttribute("categorys", categoryEntitys);
        return "index";
    }

    //  index/json/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String helloLock() {
        //1、获取一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");

        //2、加锁 阻塞时等待 默认加锁时间30s
//        lock.lock();

        //1）、锁的自动续期，如果业务超长，运行期间自动锁上新的30s。不用担心业务时间长，锁自动过期被删掉
        //2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题

        lock.lock(10, TimeUnit.SECONDS);   //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
        //问题：lock.lock(10, TimeUnit.SECONDS);在锁时间到了以后，不会自动续期

        //1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们制定的时间
        //2、如果我们指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
        //  只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒
        //  internalLockLeaseTime 【看门狗时间】 / 3， 10s

        //最佳实战：
        //1）、推荐使用 lock.lock(10, TimeUnit.SECONDS); 省掉了整个续期操作。手动解锁

        try {
            //业务
            System.out.println("加锁成功，执行业务……");
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            //解锁
            lock.unlock();
        }

        return "helloLock";
    }

    //写数据 ---读写锁：保证一定能读到最新数据。修改期间，写锁是一个排他锁，读锁是共享锁
    @GetMapping("/write")
    @ResponseBody
    public String writeValue(){

        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            //业务 -- 该数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //解锁
            rLock.unlock();
        }

        return s;
    }

    //读数据
    @GetMapping("/read")
    @ResponseBody
    public String readValue(){
        String s = "";
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = readWriteLock.readLock();
        try {
            rLock.lock();
            ValueOperations<String, String> ops = redisTemplate.opsForValue();
            s = ops.get("writeValue");
//            try { TimeUnit.SECONDS.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 放假，锁门
     * 1 班没人了，2
     * 5个班全部走完，我们可以锁大门
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        //闭锁
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();//等待闭锁完成
        return "放假了……";
    }

    @ResponseBody
    @GetMapping("/gogo/{id}")
    public String gogo(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();//计数减一
        return id + "班的人都走了……";
    }

    /**
     * 车库停车
     * 3停车位
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.acquire();//获取一个信号，获取一个值
        return "停车占用车位ok";
    }

    //车开走
    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个车位
        return "释放车位ok";
    }

}
