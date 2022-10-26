package com.atguigu.gulimall.search.thread;

import com.zaxxer.hikari.util.UtilityElf;

import java.util.concurrent.*;

/**
 * @author:厚积薄发
 * @create:2022-10-22-19:34
 */
public class ThreadTest {

    private static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main……start");
        /**
         * 1) 、继承Thread
         *      Thread01 thread = new Thread01();
         *      thread.start();
         *
         * 2) 、实现Runnable接口
         *      Runable01 runable01 = new Runable01();
         *      new Thread(runable01).start();
         *
         * 3)、实现callable接口÷ FutureTask(可以拿到返回结果，可以处理异常)
         *      FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
         *      new Thread(futureTask).start();
         *      //阻塞等待整个线程执行完成，获取返回结果
         *      Integer integer = futureTask.get();
         *
         * 4) 、线程池 [ExecutorService]
         *         给线程池直接提交任务
         *         业务代码中，以上三种启动线程方式都不用【应将所有的多线程异步任务都交给线程池执行】
         *         1、创建：
         *              1> Executors 工具类
         *
         * 区别：
         *      1、2不能得到返回值，3可以获取返回值
         *      1、2、3都不能控制资源
         *      4可以控制资源（指定线程池线程数）性能稳定
         */

        //当前系统中池只有一两个，每个异步任务，提交给线程池自己执行
        service.execute(new Runable01());

        /**
         * 七大参数
         * 1.corePoolSize：核心线程数[一直存在，除非设置allowCoreThreadTimeOut]，线程池，创建好以后就准备就绪的线程数量，就等带来接收异步任务去执行
         * 2.maximumPoolSize：最大线程数量；控制资源并发
         * 3.keepAliveTime:存活时间，如果当前的线程数量大于core数量。释放空闲的线程（maximumPoolSize - corePoolSize;只要线程空闲大于指定的keepAliveTime）
         * 4.unit：时间单位
         * 5.BlockingQueue<Runnable> workQueue：阻塞队列。如果任务有很多，就会将目前多出的任务放在队列里面，只要有线程空闲，就会去队列里面取出新的任务继续执行
         * 6.threadFactory：线程的创建工厂
         * 7.RejectedExecutionHandler handler：如果队列满了，按照我们指定的拒绝策略拒绝执行任务
         *
         * 工作顺序：
         *      1）、线程池创建， 准备好 core 数量的核心线程， 准备接受任务
         *      2）、新的任务进来， 用 core 准备好的空闲线程执行。
         *          2.1> core 满了， 就将再进来的任务放入阻塞队列中。 空闲的 core 就会自己去阻塞队
         *      列获取任务执行
         *          2.2> 阻塞队列满了， 就直接开新线程执行， 最大只能开到 max指定的数量
         *          2.3> max 都执行好了。 Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自
         *      动销毁。 最终保持到 core 大小
         *          2.4> 如果线程数开到了 max 的数量， 还有新任务进来， 就会使用 reject 指定的拒绝策
         *      略进行处理
         *          new LinkedBlockingQueue<>()：默认是Integer的最大值。容易内存移除
         *  面试题： 一个线程池 core 7； max 20 ， queue： 50， 100 并发进来怎么分配的；
         *      先有 7 个能直接得到执行， 接下来 50 个进入队列排队， 在多开 13 个继续执行。 现在 70 个
         * 被安排上了。 剩下 30 个默认拒绝策略。
         *
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

//        Executors.newCachedThreadPool();//core核心是0，所有都可回收
//        Executors.newFixedThreadPool();//固定大小，core=max,都不可回收
//        Executors.newScheduledThreadPool();//做定时任务的线程池
//        Executors.newSingleThreadExecutor();//单线程的线程池，后台从队列里面获取任务，挨个执行

        System.out.println("main……end");
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

}
