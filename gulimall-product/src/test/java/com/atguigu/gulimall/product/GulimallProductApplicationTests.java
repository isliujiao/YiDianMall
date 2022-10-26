package com.atguigu.gulimall.product;



import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    SkuInfoService skuInfoService;

    @Test
    public void testgg(){

    }

    @Test
    public void test(){
//        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(4L, 225L);
//        System.out.println(attrGroupWithAttrsBySpuId);

        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(4L);
        System.out.println(saleAttrsBySpuId);
    }

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void testStirngRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello","world" + UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    public void testFindPath(){
        System.out.println("123" + 1);
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        /*保存*/
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
//        brandService.save(brandEntity);

        /*修改*/
//        brandEntity.setDescript("华为手机");
//        brandEntity.setBrandId(1L);
//        brandService.updateById(brandEntity);

        /*条件查询*/
        QueryWrapper<BrandEntity> qw = new QueryWrapper<>();
        qw.eq("brand_id", 1L);
        List<BrandEntity> list = brandService.list(qw);
        list.forEach((item) -> {
            System.out.println(item);
        });

    }

}
