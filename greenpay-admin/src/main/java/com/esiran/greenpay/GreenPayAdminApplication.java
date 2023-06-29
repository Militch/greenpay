package com.esiran.greenpay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.esiran.greenpay.**.mapper")
public class GreenPayAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenPayAdminApplication.class,args);
    }
}
