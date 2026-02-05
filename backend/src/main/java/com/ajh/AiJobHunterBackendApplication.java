
package com.ajh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class AiJobHunterBackendApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(AiJobHunterBackendApplication.class, args);
    }
}