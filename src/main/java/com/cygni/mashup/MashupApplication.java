package com.cygni.mashup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MashupApplication {

    public static void main(String[] args) {
        SpringApplication.run(MashupApplication.class, args);
    }

}
