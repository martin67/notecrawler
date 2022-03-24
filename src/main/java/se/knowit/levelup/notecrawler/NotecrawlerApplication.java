package se.knowit.levelup.notecrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class NotecrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotecrawlerApplication.class, args);
    }

}
