package com.negotiatex.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Uses default ConcurrentMapCacheManager or RedisCacheManager whichever is available
}
