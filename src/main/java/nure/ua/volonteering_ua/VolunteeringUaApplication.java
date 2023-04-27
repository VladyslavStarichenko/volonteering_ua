package nure.ua.volonteering_ua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication
public class VolunteeringUaApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteeringUaApplication.class, args);
    }

}

