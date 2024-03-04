package org.ntranlab.url.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConfigs {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configs = new RedisStandaloneConfiguration();
        configs.setDatabase(0);
        configs.setHostName(host);
        configs.setPassword(password);
        configs.setPort(port);
        return new LettuceConnectionFactory(configs);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(connectionFactory);
        return t;
    }
}
