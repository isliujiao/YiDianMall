YiDianMall

#### 介绍
一点生活购物
本系统采用微服务架构设计，在分布式环境下利用Spring Cloud框架，设计独立模块的微服务，拆分为商品服务、订单服务、
认证服务、购物车服务、检索服务等模块，结合了当前比较流行的互联网B2C电商模式，为消费者提供商品贸易平台。
商城项目是一个电商平台，，提供商品购买、订单管理、支付等功能。用户可以在平台上注册登录、浏览商品、下单、付款等  。结合了当前比较流行的互联网B2C电商模式，为消费者提供商品贸易平台。
#### 软件架构
- 前端：使用 Vue.js、Element UI、Axios 等技术实现。

- 后端：使用 Spring Boot、MyBatis、Spring Cloud、等技术实现。

- 数据库：使用 MySQL 数据库存储数据。

- 缓存：使用 Redis 实现缓存，提高数据访问效率。

- 消息队列：使用 RabbitMQ 实现异步消息处理，提高系统吞吐量。

在项目实现过程中，我们采用前后端分离的方式，前端与后端通过 RESTful API 进行数据交互。同时，我们也采用 GitLab 进行版本控制和持续集成，通过自动化测试和部署，保证代码质量和系统稳定性。

#### 商城项目分布式锁如何实现的？

##### 1. 使用Redis实现分布式锁

在Redis中，可以使用**SETNX**命令（set if not exists）实现分布式锁。具体实现步骤如下：

- 当一个客户端想要获取锁时，它会向Redis中写入一个特定的键值对，其中键是锁的名称，值是客户端的标识符，并设置一个过期时间。
- 如果Redis中没有这个键值对，客户端就可以获取锁。如果Redis中已经有这个键值对，客户端就不能获取锁。
- 当客户端完成任务后，它会向Redis发送一个DELETE命令来删除这个键值对，从而释放锁。

**2. 使用Redisson实现分布式锁**

Redisson是一个基于Redis实现的Java分布式锁框架，提供了更加便捷的分布式锁实现方式。具体实现步骤如下：

- 使用RedissonClient对象获取**rlock**对象。
- 调用Rlock对象的lock方法获取锁，如果获取成功，则可以执行业务逻辑；如果获取失败，则等待一定时间后重试。
- 当业务逻辑执行完成后，调用Rlock对象的**unlock**方法释放锁。




### 记录文件

##### - MyBaties-Plus

```
* 1、整合mybatis-plus
*  1）导入依赖
*      <dependency>
*          <groupId>com.baomidou</groupId>
*          <artifactId>mybatis-plus-boot-starter</artifactId>
*          <version>3.2.0</version>
*      </dependency>
*  2）配置
*      1、配置数据源
*          1>导入数据库驱动
*          2>application.yml配置数据源相关信息
*      2、配置mybatis-plus；
*          1>使用@MapperScan扫描
*          2>告诉mybatis-plus，sql映射文件的位置（application.yml），并统一设置主键自增
```

##### - 逻辑删除

```
*      1）、配置全局的逻辑删除规则
*      2）、配置逻辑删除的组件Bean
*      3）、给Bean加上逻辑删除注解@TableLogic
```

##### - JSR303 数据校验

```
JSR303
*      1)、给Bean添加校验注解：javax.validation.constraints,定于自己的message信息
*      2)、开启校验功能，在需要校验控制类传输参数上 @Valid
*        效果：校验错误以后有默认的响应
*      3)、紧跟BindingResult,就可以获取校验的结果
*      4)、分组校验功能
*          1> @NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})   //必须包含非空字符
*             给校验注解标注什么时候需要进行校验
*          2> @Validated({AddGroup.class})
*             在Controller类 标注使用哪组校验
*          3> 默认没有指定分组校验注解在分组校验情况下不生效 ！
*      5)、自定义检验
*          1>编写一个自定义检验注解
*          2>编写一个自定义检验器    ConstraintValidator
*          3>关联自定义的校验注解和检验器
*   @Documented
*   @Constraint(validatedBy = {ListValueConstraintValidator.class,【可以指定多个不同的校验器】})
*   @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
*   @Retention(RetentionPolicy.RUNTIME)
*   public @interface ListValue {
```

##### - 统一异常处理

```
* 统一异常处理
* @CibtrollerAdvice
*  1)、编写异常处理类，使用@CibtrollerAdvice
*  2)、使用@ExceptionHandler标注方法可以处理的异常
```

##### -  模板引擎 thymeleaf

```
* 模板引擎
*      1)、引入thymeleaf-starter，关闭缓存
*      2)、静态资源都泛着该static文件夹下就可以按照路径直接访问
*      3)、页面放在templates下，直接访问（Springboot默认访问index）
*      4)、页面修改不重启服务器实时更新
*          1> 引入dev-tools
*          2> 修改完页面 shift F9 --> build project重新构建
```

##### - Redis

```
* 整合Redis
*   1)、引入data-redis-starter
*   2)、简单配置redis的host等信息
*   3)、使用SpringBoot自动配置好的StringRedisTemplate来操作redis
*      redis --> Map(Map相当于本地单线程缓存，redis可以分布式多线程缓存)
```

##### - Redisson 分布式锁

```
*   1）、引入依赖
*          <dependency>
*             <groupId>org.redisson</groupId>
*             <artifactId>redisson</artifactId>
*             <version>3.12.0</version>
*         </dependency>
*   2）、配置redisson
*      MyRedissonConfig给容器中配置一个RedissonClient实例即可
*   3)、使用
*      参照文档做
```

##### - SpringCache

```
*      1)、引入依赖
*            <artifactId>spring-boot-starter-cache</artifactId>
*      2)、写配置哦
*          (1)、自动配置了哪些
*              CacheAuroConfiguration会导入RedisCacheConfiguration
*              自动配置好了缓存管理器RedisCacheManager
*          (2)、配置使用redis作为缓存：spring.cache.type=redis
*      3)、测试使用缓存
*              @Cacheable：触发将数据保存到缓存的操作
*              @CacheEvict：出发将数据从缓存删除的操作
*              @CachePut： 不影响方法执行更新缓存
*              @Caching：组合以上多个操作
*              @CacheConfig：在类级别共享缓存的相同配置
*              1)、开启缓存功能 @EnableCaching
*              2)、只需要使用注解就能完成缓存操作
*      4)、原理：
*          CacheAutoConfiguration ->RedisCacheConfiguration ->
*          自动配置了RedisCacheManager->初始化所有的缓存->每个缓存决定使用什么配置->
*          ->如果redisCacheConfiguration有就用已有的，没有就用默认配置
*          ->想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可-
*          >就会应用到当前RedisCacheManager管理的所有缓存分区中
```

##### - OSS文件存储

```
/**
 * 1、引入oss-starter
 * 2、配置key、endpoint相关信息即可
 * 3、使用OSSClient 进行相关操作
 */
```

##### -  整合Elastic Search

```
/**
 * 1、导入依赖
 * 2、编写配置,给容器中注入一个RestHighLevelClient
 * 3、参照API
 */
```

##### - 异步（多线程实现方式）

```
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
 
 -- 线程池七大参数 --
 /**
 * 1.corePoolSize：核心线程数[一直存在，除非设置allowCoreThreadTimeOut]
 * 线程池，创建好以后就准备就绪的线程数量，就等带来接收异步任务去执行
 * 2.maximumPoolSize：最大线程数量；控制资源并发
 * 3.keepAliveTime:存活时间，如果当前的线程数量大于core数量。
 * 释放空闲的线程（maximumPoolSize - corePoolSize;只要线程空闲大于指定的keepAliveTime）
 * 4.unit：时间单位
 * 5.BlockingQueue<Runnable> workQueue：阻塞队列。如果任务有很多，就会将目前多出的任务放在队列里
 * 面， 只要有线程空闲，就会去队列里面取出新的任务继续执行
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
```

##### - Spring Session

```
/**
 * SpringSession核心原理：
 *   1、@EnableRedisHttpSession 导入 RedisHttpSessionConfiguration配置
 *      1）、该配置给容器中添加了一个组件`RedisOperationsSessionRepository`
 *          SessionRepository --> RedisOperationsSessionRepository --> redis操作session的增删改查
 *      2）、继承 SessionRepositoryFilter ==> Filter：session存储过滤器；每一个请求过来都必须经过filter
 *              ①创建的时候，就自动从容器中获取到了 SessionRepository
 *              ②原生的request、response都被包装。--> SessionRepositoryRequestWrapper、SessionRepositoryResponseWrapper
 *              ③以后获取session。request.getSession();（被重写）
 *              ④wrappedRequset.getSession(); ---> SessionRepository中获取到的
 *  装饰着模式
 *  自动延期。redis中的数据也有过期时间
 */
```

##### - 整合Sentienl

```
/**
 * 1.整合Sentienl
 *      1）、导入依赖：spring-cloud-alibaba-sentinel
 *      2）、下载sentienl控制台
 *      3）、配置Sentinel控制台地址信息
 *      4）、在控制台调整参数。【默认所有的流控设置保存在内存中，重启失效】
 * 2、每一个微服务都导入actuator;并配置management.endpoints.web.exposure.include=*
 * 3、自定义sentinel流控返回数据
 *
 * 4、使用Sentinel来保护Feign远程调用，熔断：
 *      1）、调用方(gulimall-product)的熔断保护：feign.sentinel.enabled=true
 *      2）、调用方手动指定远程服务的降级策略。远程服务被降级处理。触发我们的熔断回调方法
 *      3）、并发访问量过大时，必须牺牲一些远程服务。在服务的提供方（远程服务）指定降级策略；
 *      提供方是在运行。但是不运行自己的业务逻辑，返回的是默认的降级数据（限流的数据）
 *
 * 5、自定义受保护的资源
 *      1）、代码
 *           try (Entry entry = SphU.entry("seckillSkus")) {
 *              业务逻辑
 *           }catch(Execption e){}
 *      2）、注解.
 *         @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
 *      无论是1、2方式都要配置被限流以后的默认返回
 *      url请求可以设置统一返回：WebCallBackManager
 *
 */
```

接口地址：[https://easydoc.net/s/78237135/ZUqEdvA4/hKJTcbfd](https://easydoc.net/s/78237135/ZUqEdvA4/hKJTcbfd)
