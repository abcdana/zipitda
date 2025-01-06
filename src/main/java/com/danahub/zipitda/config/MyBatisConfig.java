package com.danahub.zipitda.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 매퍼 XML 파일 경로 설정
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml");
        sessionFactory.setMapperLocations(mapperLocations);

        return sessionFactory.getObject();
    }
}