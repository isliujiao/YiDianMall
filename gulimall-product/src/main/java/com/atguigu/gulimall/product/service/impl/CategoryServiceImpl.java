package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类(没有任何查询条件-null)
        /**
         * 因为本类继承了ServiceImpl，‘protected M baseMapper;’
         * 所以baseMapper就是注入的CategoryDao,用以操作数据库
         */
        List<CategoryEntity> entities = baseMapper.selectList(null);//查出所有，

        //2.组装成父子的树形结构
        //fileter:过滤，.collect(Collectors.toList())：收集成一个集合
        List<CategoryEntity> levelMenus1 = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {//将当前菜单的子分类保存进去
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {  //排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return levelMenus1;
    }

    /**
     * 批量删除方法
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前被删除的菜单，是否在其他地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }


    /**
     * 级联更新所有关联数据,不仅更新自己，还要更新级联。开启事务
     *      1、同时进行多种缓存操作 ———— @Caching
     *      2、指定删除某个分区下的所有数据线 @CacheEvict(value = "category",allEntries = true)
     *  存储同一类型的数据，都可以指定成同一分区。这样就可以批量删除。
     *  开启使用前缀（默认是缓存名），分区名默认就是缓存的前缀
     */
//    @CacheEvict(value = "category", key = "'getLevel1Categorys'") //更新数据库时将缓存中的文件删掉
//    @Caching(evict = {          //批量操作
//            @CacheEvict(value = "category", key = "'getCatalogJson'"),
//            @CacheEvict(value = "category", key = "'getLevel1Categorys'")
//    })
    @CacheEvict(value = "category",allEntries = true)   //失效模式
//    @CachePut  //双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 1、每一个需要缓存的数据我们都来治党要放到哪个名字的缓存。【缓存的分区】
     * 2、@Cacheable({"category"})
     *      代表当前方法的结果需要缓存，如果缓存中有，方法不用调用
     *      如果缓存中没有，会调用方法，最后将方法的结果放入缓存
     * 3、默认行为
     *      1）、如果缓存中有，方法不第哦啊用
     *      2）、key默认自动生成；缓存的名字::SimpleKey[]{自主生成的key值}
     *      3）、缓存的value的值。默认使用jdk序列化机制，将序列化后的数据存到redis
     *      4）、默认ttl时间 -1(永不过期)
     *      自定义
     *          1）、指定生成的缓存使用的key： key属性指定，接收一个SpEl
     *          2）、指定缓存的数据的存活时间：配置文件中修改ttl
     *          3）、将数据保存为json格式
     * 4、SpringCache的不足
     *      1）、读模式：
     *          缓存穿透：spring.cache.redis.cache-null-values=true
     *          缓存击穿：默认不加锁
     *          缓存雪崩：spring.cache.redis.time-to-live=3600000
     *      2）、写模式：（缓存与数据库一致）
     *          (1)、读写加锁 (2)、引入Canal，感知MySQL的更新 (3)读多写多，直接操作数据库
     *
     *     总结：
     *          常规数据（读多写少，即时性，一致性要求不高的数据）完全可以使用Spring-Cache
     *          特殊数据：特殊设计
     *     底层原理：CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
     */
    //获取一级分类；代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有，会调用方法，最后将方法的结果放入缓存
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys……");
        //查出一级分类，父id为0
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                .eq("parent_cid", 0));
        return categoryEntities;
    }

    //使用SpringCache的@Cacheable注解进行三级分类缓存
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库……");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1、查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }

    /**
     * TODO 产生堆外内存溢出OutOfDirectMemoryError
     * 1)、springboot2.0以后默认使用lettuce操作redis的客户端，它使用netty进行网络通信
     * 2)、lettuce的bug导致netty堆外内存溢出   可设置：-Dio.netty.maxDirectMemory
     * 解决方案：不能直接使用-Dio.netty.maxDirectMemory去调大堆外内存
     * 1)、升级lettuce客户端。      2）、切换使用jedis
     */
    //查询二、三级分类（使用缓存）
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型；【序列化与反序列化】

        /** 缓存问题
         * 1、空结果缓存：解决缓存穿透
         * 2、设置过期时间（加随机值）：解决缓存雪崩
         * 3、加锁：解决缓存穿击
         */

        //1、加入缓存逻辑,缓存中存的数据是json字符串
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");

        //2.1、缓存中没有，查询数据库
        if (StringUtils.isEmpty(catalogJSON)) {
            //保证数据库查询完成以后，将数据放在redis中，是一个原子操作
            System.out.println("缓存不命中……将要查询数据库");
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            return catalogJsonFromDb;
        }

        System.out.println("缓存命中……直接返回……");
        //2.2、缓存中有，转换为指定对象直接返回
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }

    //使用Redisson，进行分布式加锁
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {

        //1、占分布式锁 —— 用Redisson设置锁   (锁的名字 -- 锁的粒度，越细越不容易出错、快速)
        //锁的粒度：具体缓存的是某个数据，商品： product-11-lock  product-12-lock
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        //加锁成功…… 执行业务 ---- getDataFromDb
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return getDataFromDb();

    }

    //使用Redis，进行分布式加锁
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        //1、占分布式锁 —— 用Redis占坑 (在占锁的同时并设置过期时间)
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            //加锁成功…… 执行业务 ---- getDataFromDb
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //删除锁2 --- 使用 lua脚本解锁  将查询和删除锁视为原子
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }

/*          //删除锁1 --- 存在风险，查询锁和删除锁不是原子操作
            String lockValue = redisTemplate.opsForValue().get("lock");
            if(uuid.equals(lockValue)){
                //判断uuid唯一标识和查到的值是否一致，保证删自己的锁
                redisTemplate.delete("lock");
            }*/

            return getDataFromDb();
        } else {
            //加锁失败,休眠100ms重试…… 。synchronized()
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock(); //自旋
        }
    }

    //查询二、三级分类(未经过缓存设置，直接从数据库中查找并封装数据)，本地锁
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {

        /**
         * 只要是同一把锁，就能锁住需要这个锁的所有线程
         * 1、synchronized(this) :SpringBoot所有的组件在容器中都是单例的
         */
        synchronized (this) {
            //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }
    }

    //抽取方法，————>业务，从数据库中获取数据
    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            //缓存不为空，转换为指定对象直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }

        System.out.println("查询了数据库……");

        /**
         * 优化：
         * 1、将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //1、查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        //3、将查到的数据放入缓存中,将查出的对象转换为json放在缓存中
        String jsonString = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //抽取的收集方法：获取父类id
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>()
//                .eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    /**
     * 找到catelogId的完整路径
     * 【父/子/孙】
     * [2,25,225]
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();//构建一个list路径
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        //按id查出当前分类的信息
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}