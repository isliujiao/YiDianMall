package com.atguigu.gulimall.cart.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author:厚积薄发
 * @create:2022-10-31-16:13
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";

    //添加购物车
    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            CartItem cartItem = new CartItem();
            //新添加购物车商品(购物车无此商品执行以下方法)
            //异步执行
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                //todo 1、远程查询当前要添加的商品的信息
                R skuinfo = productFeignService.getSkuinfo(skuId);
                SkuInfoVo data = skuinfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });

                //2、商品添加到购物车
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                //todo 3、远程查出sku属性的组合信息
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);

            //异步，getSkuInfoTask、getSkuSaleAttrValues任务完成后继续执行
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            //给Redis存储数据（购物车添加后Redis也保存。key：商品的Id，value：商品属性参数）
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        } else {
            //购物车`已经有`此商品，修改商品数量即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);

            //修改Redis(key:商品skuId,value:商品信息)
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(str, CartItem.class);
        return cartItem;
    }

    //获取购物车所有数据
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        //1、判断哪种状态的购物车
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        if (userInfoTo.getUserId() != null) {
            //1、登录
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            //2、如果临时购物车的数据还没有进行合并【合并购物车】
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null) {
                //临时购物车有数据，需要合并到用户购物车中
                for (CartItem item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                //清空购物车
                clearCart(tempCartKey);
            }

            //3、获取登录后的购物车的数据【包含合并过来的临时购物车的数据，&& 登录后的购物车的数据】
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            //2、未登录,获取临时购物车的所有购物项
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }

        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        String s = JSON.toJSONString(cartItem);
        //更改Redis
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    //获取用户购物车购物项
    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);

            //获取所有被选中的购物项
            List<CartItem> collect = cartItems.stream()
                    .map(item -> {
                        //todo 远程查询根据id查询商品价格(防止购物车价格过期)
                        R price = productFeignService.getPrice(item.getSkuId());
                        String data = (String) price.get("data");
                        item.setPrice(new BigDecimal(data));
                        return item;
                    })
                    .filter(item -> item.getCheck())
                    .collect(Collectors.toList());

            return collect;
        }
    }

    /**
     * 抽取方法，获取要操作的购物车（用户、临时）存储在Redis
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //1、在拦截器获取用户信息，查看用户是否登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        //2、查看使用用户购物车还是临时购物车
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            //登录的值：gulimall:cart:[商品skuId]
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            //没登录的值：gulimall:cart:xxxx(临时用户)
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        //操作Redis，对cartKey进行绑定hash操作，只需要操作operations即可对cartKey的属性进行操作
        BoundHashOperations<String, Object, Object> operations
                = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    /**
     * 方法：获取购物车中所有购物项
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = boundHashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}
