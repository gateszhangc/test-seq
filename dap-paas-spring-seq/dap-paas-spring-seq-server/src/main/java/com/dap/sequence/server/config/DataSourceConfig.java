package com.dap.sequence.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: cao
 * Data: 2022/2/28 16:16
 * @Descricption: 默认数据源添加进去
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String defaultDBUrl;
    @Value("${spring.datasource.username}")
    private String defaultDBUser;
    @Value("${spring.datasource.password}")
    private String defaultDBPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String defaultDBDriverName;


    @Bean
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
        try {
            DruidDataSource defaultDataSource = new DruidDataSource();
            defaultDataSource.setUrl(defaultDBUrl);
            defaultDataSource.setUsername(defaultDBUser);
            defaultDataSource.setPassword(defaultDBPassword);
            defaultDataSource.setDriverClassName(defaultDBDriverName);
            Map<Object, Object> map = new HashMap<>();
            map.put("default", defaultDataSource);
            dynamicDataSource.setTargetDataSources(map);
            dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dynamicDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("dynamicDataSource") DataSource dynamicDataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dynamicDataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
