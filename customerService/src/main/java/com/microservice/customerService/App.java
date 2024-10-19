package com.microservice.customerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
//@SpringBootApplication
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }) //if we are not connecting with any DB
@EnableFeignClients
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}