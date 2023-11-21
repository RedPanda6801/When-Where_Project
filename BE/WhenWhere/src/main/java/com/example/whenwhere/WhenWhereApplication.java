package com.example.whenwhere;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class WhenWhereApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhenWhereApplication.class, args);
    }

}
