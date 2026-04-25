package ru.itis.dis403.lab2_8.publisher;

import io.nats.client.Connection;
import io.nats.client.Nats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WeatherPublisherService {

    private Connection nc;

    @PostConstruct
    public void connect() throws Exception {
        nc = Nats.connect("nats://nats:4222");
        System.out.println("Подключились к NATS");
    }

    @Scheduled(fixedRate = 1000)
    public void publish() throws Exception {
        String json = "{\"city\":\"Kazan\",\"temp\":18.5,\"pressure\":1013.0," +
                "\"windSpeed\":4.2,\"windDirection\":\"North\",\"dateTime\":\""
                + LocalDateTime.now() + "\"}";
        nc.publish("Weather", json.getBytes());
        System.out.println("Опубликовано");
    }

    @PreDestroy
    public void disconnect() throws Exception {
        if (nc != null) nc.close();
    }
}