package com.umc.sp.contents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class UmcSpContentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UmcSpContentsApplication.class, args);
    }

}
