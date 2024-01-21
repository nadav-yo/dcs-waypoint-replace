package org.faulty.wpreplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
class WpShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(WpShellApplication.class, args);
    }
}
