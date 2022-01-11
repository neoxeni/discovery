package com.mercury.discovery.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile("!jndi")
@Configuration
@Slf4j
public class DatabaseConfig {
    @PostConstruct
    public void init() {
        log.info("default Datasource hikari connect");
    }

    @Bean("hikariConfig")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource(hikariConfig());

        log.info("spring.datasource.groupware config : {}", dataSource);
        log.info("connectionTestQuery:{}", dataSource.getConnectionTestQuery());
        log.info("connectionTimeout:{}", dataSource.getConnectionTimeout());
        log.info("maximumPoolSize:{}", dataSource.getMaximumPoolSize());
        log.info("maxLifetime:{}", dataSource.getMaxLifetime());
        log.info("minimumIdle:{}", dataSource.getMinimumIdle());
        log.info("idleTimeout:{}", dataSource.getIdleTimeout());

        return new LazyConnectionDataSourceProxy(dataSource);
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
