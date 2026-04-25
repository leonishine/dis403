package ru.itis.dis403.lab2_8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.itis.dis403.lab2_8.controllers.IndexController;
import ru.itis.dis403.lab2_8.publisher.WeatherPublisher;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        if (args.length > 0 && "publisher".equals(args[0])) {
            WeatherPublisher.main(args);
            return;
        }

        SpringApplication.run(Application.class, args);
    }
}