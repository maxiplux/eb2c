package app.quantun.eb2c.config.redis;

import app.quantun.eb2c.message.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Configuration class for Redis.
 *
 * This class sets up the Redis configuration for the application, including the RedisTemplate,
 * RedisMessageListenerContainer, and ChannelTopic. The configurations are based on the application
 * properties provided in the application.properties file.
 */
@Configuration
public class RedisConfig {

    /**
     * Configures the RedisMessageListenerContainer bean.
     *
     * This method sets up the RedisMessageListenerContainer with the provided RedisConnectionFactory
     * and MessageListenerAdapter. It also subscribes to the specified ChannelTopic.
     *
     * @param connectionFactory the Redis connection factory
     * @param listenerAdapter the message listener adapter
     * @return RedisMessageListenerContainer instance
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, topic());
        return container;
    }

    /**
     * Configures the MessageListenerAdapter bean.
     *
     * This method sets up the MessageListenerAdapter with the provided RedisSubscriber.
     * The RedisSubscriber's onMessage method will be called when a message is received.
     *
     * @param subscriber the Redis subscriber
     * @return MessageListenerAdapter instance
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    /**
     * Configures the ChannelTopic bean.
     *
     * This method sets up the ChannelTopic with the specified topic name.
     * The topic name is used to subscribe to the Redis message queue.
     *
     * @return ChannelTopic instance
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("messageQueue");
    }

    /**
     * Configures the RedisTemplate bean.
     *
     * This method sets up the RedisTemplate with the provided RedisConnectionFactory.
     * The RedisTemplate is used for performing Redis operations.
     *
     * @param connectionFactory the Redis connection factory
     * @return RedisTemplate instance
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
