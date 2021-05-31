package com.lixl.sc.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.lixl.sc.POJO.CaffeineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: CaffeineCacheManager
 * @Description:
 * @Author: lixl
 */
public class CaffeineCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineCacheManager.class);

    private String cachePrefix;
    private boolean dynamic = true;
    private Set<String> cacheNames;
    private boolean cacheNullValues = true;
    private CaffeineProperties caffeineProperties;
    private RedisTemplate<Object, Object> stringKeyRedisTemplate;

    private ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    /**
     * 缓存参数的分隔符
     * 数组元素0=缓存的名称
     * 数组元素1=缓存过期时间TTL
     * example:@Cacheable(key = "'userList'+ #cityId", value = "localCache_user#5#100", cacheManager = "caffeineCacheManager", sync = true)
     */
    private static String separator = "#";

    public CaffeineCacheManager(RedisTemplate<Object, Object> stringKeyRedisTemplate,
                                CaffeineProperties caffeineProperties,
                                Set<String> cacheNames, String cachePrefix, boolean dynamic, boolean cacheNullValues) {
        super();
        this.caffeineProperties = caffeineProperties;
        this.dynamic = dynamic;
        this.cacheNames = cacheNames;
        this.cachePrefix = cachePrefix;
        this.cacheNullValues = cacheNullValues;
        this.stringKeyRedisTemplate = stringKeyRedisTemplate;
    }

    @Override
    public Cache getCache(String cacheName) {

        String[] cacheParams = cacheName.split(this.separator);
        String name = cacheParams[0];

        Cache cache = cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        if (!dynamic && !cacheNames.contains(name)) {
            return cache;
        }

        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = null;
        if (cacheParams.length == 2) {
            caffeineCache = caffeineCache(Long.parseLong(cacheParams[1]));
        } else if (cacheParams.length == 3) {
            caffeineCache = caffeineCache(Long.parseLong(cacheParams[1]), Long.parseLong(cacheParams[2]));
        } else {
            caffeineCache = caffeineCache();
        }

        cache = new CaffeineCache(name, stringKeyRedisTemplate, cachePrefix, caffeineCache, cacheNullValues);
        Cache oldCache = cacheMap.putIfAbsent(name, cache);
        logger.info("create cache instance, the cache name is : {}", name);
        return oldCache == null ? cache : oldCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache() {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder().recordStats();
        if (caffeineProperties.getExpireAfterAccess() > 0) {
            cacheBuilder.expireAfterAccess(caffeineProperties.getExpireAfterAccess(), TimeUnit.MILLISECONDS);
        }
        if (caffeineProperties.getExpireAfterWrite() > 0) {
            cacheBuilder.expireAfterWrite(caffeineProperties.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
        }
        if (caffeineProperties.getInitialCapacity() > 0) {
            cacheBuilder.initialCapacity(caffeineProperties.getInitialCapacity());
        }
        if (caffeineProperties.getMaximumSize() > 0) {
            cacheBuilder.maximumSize(caffeineProperties.getMaximumSize());
        }
        if (caffeineProperties.getRefreshAfterWrite() > 0) {
            cacheBuilder.refreshAfterWrite(caffeineProperties.getRefreshAfterWrite(), TimeUnit.MILLISECONDS);
        }
        return cacheBuilder.build();
    }

    /**
     * @param expire 过期时间（秒）
     */
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache(Long expire) {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder().recordStats().expireAfterWrite(expire, TimeUnit.SECONDS);
        if (caffeineProperties.getMaximumSize() > 0) {
            cacheBuilder.maximumSize(caffeineProperties.getMaximumSize());
        } else {
            cacheBuilder.maximumSize(1000);
        }
        return cacheBuilder.build();
    }

    /**
     * @param expire  过期时间（秒）
     * @param maxSize 最大数量
     */
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache(long expire, long maxSize) {
        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder().recordStats().expireAfterWrite(expire, TimeUnit.SECONDS).maximumSize(maxSize);
        return cacheBuilder.build();
    }

    public void clearLocal(String cacheName, Object key) {
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            return;
        }

        CaffeineCache caffeineCache = (CaffeineCache) cache;
        caffeineCache.clearLocal(key);
    }

    public Cache getLocal(String cacheName) {
        if (cacheMap == null || cacheMap.isEmpty()) {
            return null;
        }
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            return null;
        }
        return cache;
    }
}
