package com.mercury.discovery.config.database;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile("jndi")
@Configuration
@Slf4j
@MapperScan(annotationClass = Mapper.class,
        basePackages = "${spring.datasource.default.mapper-base-packages:com.mercury.discovery.**}",
        sqlSessionFactoryRef = "sqlSessionFactory")
public class JndiDataBaseConfig {

    @Value("${spring.datasource.default.jndi-name:java:comp/env/jndi/sampleDatasource}")
    private String jndiName;

    @PostConstruct
    public void init() {
        log.info("default Datasource jndi connect [{}]", jndiName);
    }

    @Bean("mocaBaseDataSource")
    @Primary
    public DataSource mocaBaseDataSource() {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        dataSourceLookup.setResourceRef(true);
        return dataSourceLookup.getDataSource(jndiName);
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("mocaBaseDataSource") DataSource mocaBaseDataSource) {
        return new DataSourceTransactionManager(mocaBaseDataSource);
    }
}
