package app.quantun.eb2c.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisPublisher {

    private final  RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;


    public void publish(  String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Published message: {}", message);

    }
}