package app.quantun.eb2c.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisSubscriber {

    public void onMessage(String message) {
        log.info("Received message: {}", message);


        // Add your business logic here
    }
}
