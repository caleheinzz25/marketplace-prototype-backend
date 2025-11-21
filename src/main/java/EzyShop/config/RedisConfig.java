// package EzyShop.config;

// import java.time.Duration;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.cache.RedisCacheConfiguration;
// import org.springframework.data.redis.cache.RedisCacheManager;
// import org.springframework.data.redis.connection.RedisConnectionFactory;

// @Configuration
// public class RedisConfig {
//     @Bean
//     public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                 .entryTtl(Duration.ofMinutes(10)) // TTL 10 menit
//                 .disableCachingNullValues();
//         return RedisCacheManager.builder(connectionFactory)
//                 .cacheDefaults(config)
//                 .build();
//     }
// }
