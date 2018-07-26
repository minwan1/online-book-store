package com.book.config;

import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Profile("local")
public class H2Config {

    @Bean
    @ConfigurationProperties("spring.datasource") // yml의 설정값을 Set한다.
    public DataSource dataSource() throws SQLException {
        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
        return new org.apache.tomcat.jdbc.pool.DataSource();
    }
}
