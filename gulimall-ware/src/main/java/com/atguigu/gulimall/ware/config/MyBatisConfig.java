package com.atguigu.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author:厚积薄发
 * @create:2022-09-19-20:17
 */
@EnableTransactionManagement
@MapperScan("com.atguigu.gulimall.ware.dao")
@Configuration
public class MyBatisConfig {

    @Autowired
    DataSourceProperties dataSourceProperties;

    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        //设置请求的页面大于最大页后操作，true调回到首页，false继续请求 默认false
        paginationInterceptor.setOverflow(true);

        //设置最大单页限制数量，默认500条 -1不受限制
        paginationInterceptor.setLimit(1000);

        return paginationInterceptor;
    }

    /**
     * Seata 分布式事务配置数据源
     * @param dataSourceProperties
     * @return
     */
    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties){
        //properties.initializeDataSourceBuilder().type(type).build();
        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(dataSourceProperties.getName())) {
            dataSource.setPoolName(dataSourceProperties.getName());
        }
        return new DataSourceProxy(dataSource);
    }
}
