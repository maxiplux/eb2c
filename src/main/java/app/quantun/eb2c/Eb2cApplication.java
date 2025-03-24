package app.quantun.eb2c;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@Configuration
@EnableJpaAuditing


public class Eb2cApplication {

    public static void main(String[] args) {
        SpringApplication.run(Eb2cApplication.class, args);
    }

}
