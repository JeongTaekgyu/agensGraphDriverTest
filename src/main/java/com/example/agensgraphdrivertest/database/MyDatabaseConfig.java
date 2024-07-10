package com.example.agensgraphdrivertest.database;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MyDatabaseConfig {

//    @Value("${spring.datasource.target0.url}")
    private String url = "jdbc:postgresql://localhost:8432/postgres";

//    @Value("${spring.datasource.target0.username}")
    private String username = "age";

//    @Value("${spring.datasource.target0.password}")
    private String password = "age";
}
