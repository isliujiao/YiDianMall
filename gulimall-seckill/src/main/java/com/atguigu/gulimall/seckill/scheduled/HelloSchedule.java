package com.atguigu.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * 定时任务
 *      1、@EnableScheduling:开启定时任务
 *      2、@Scheduled 开启定时任务
 *      3、自动配置类： TaskSchedulingAutoConfiguration
 *
 * 异步任务
 *      1、@EnableAsync 开启异步任务功能
 *      2、@Async 给希望异步执行的方法上标注
 *      3、自动配置类： TaskExecutionAutoConfiguration
 *
 * @author:厚积薄发
 * @create:2022-11-11-16:33
 */
@Slf4j
@Component
//@EnableAsync
//@EnableScheduling
public class HelloSchedule {

    /**
     * 1、Spring中6位组成，不允许第7位的年
     * 2、再周几的位置，1-7代表周一导周日，或MON-SUN
     * 3、定时任务不应该阻塞。默认是阻塞的
     *      1）、可以让业务运行以异步的方式，自己提交导线程池
     *         CompletableFuture.runAsync(() ->{
     *             xxxService.hello();
     *         },exxcutor);
     *      2）、支持定时任务线程池；设置TaskSchedulingProperties
     *           spring.task.scheduling.pool.size=5
     *      3）、开启使用异步任务@EnableAsync，异步任务注解：@Async
     * 解决：使用异步+定时任务来完成定时任务不阻塞的功能
     */
    @Async
    @Scheduled(cron = "* * * * * ?") // 定时任务；cron表达式(秒 分 时 日 月 周)
    public void hello() throws InterruptedException {
//        log.info("hello……{}",new Date());
//        Thread.sleep(3000);

    }

}
