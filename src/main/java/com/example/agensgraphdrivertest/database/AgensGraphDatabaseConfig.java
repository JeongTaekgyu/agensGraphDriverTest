package com.example.agensgraphdrivertest.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
public class AgensGraphDatabaseConfig {

    @Bean
    public Connection agensGraphConnection() throws ClassNotFoundException, SQLException {
        Class.forName("net.bitnine.agensgraph.Driver");
        String connectionString = "jdbc:agensgraph://localhost:6439/postgres";
        String username = "agens";
        String password = "agens";
        return DriverManager.getConnection(connectionString, username, password);
    }

    @Bean
    public JdbcTemplate agensGraphJdbcTemplate(@Qualifier("agensGraphConnection") Connection connection) {
        return new JdbcTemplate(new SingleConnectionDataSource(connection, true));
    }

    @Bean
    public SimpleJdbcCall agensGraphSimpleJdbcCall(@Qualifier("agensGraphConnection") Connection connection) {
        return new SimpleJdbcCall(new SingleConnectionDataSource(connection, true));
    }

//    @Bean
//    public HikariConfig target1HikariConfig() {
//        HikariConfig hikariConfig = new HikariConfig();
//
//        hikariConfig.setJdbcUrl("jdbc:postgresql://%s:%s/%s".formatted("localhost", "6439",
//                "postgres"));
//        hikariConfig.setUsername("agens");
//        hikariConfig.setPassword("agens");
//
//        return hikariConfig;
//    }
//
//    @Bean
//    public HikariDataSource target1HikariDataSource() {
//        System.out.println("target1HikariDataSource() called");
//        return new HikariDataSource(target1HikariConfig());
//    }
//
//    @Bean
//    public JdbcTemplate target1JdbcTemplate(@Qualifier("target1HikariDataSource") HikariDataSource dataSource) {
//        System.out.println("target1JdbcTemplate() called");
//        System.out.println("~~~database url: " + dataSource.getJdbcUrl());
//        System.out.println("~~~database username: " + dataSource.getUsername());
//        System.out.println("~~~database password: " + dataSource.getPassword());
//        return new JdbcTemplate(dataSource);
//    }
//
//    @Bean
//    public SimpleJdbcCall target1SimpleJdbcCall(@Qualifier("target1HikariDataSource") HikariDataSource dataSource) {
//        System.out.println("target1SimpleJdbcCall() called");
//        return new SimpleJdbcCall(dataSource);
//    }
}
