package com.esiran.greenadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.esiran.greenadmin.**.mapper")
public class GreenAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenAdminApplication.class,args);
    }
}
