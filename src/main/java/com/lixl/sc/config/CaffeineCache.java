package com.lixl.sc.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class CaffeineCache extends AbstractValueAdaptingCache {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineCache.class);

    private String name;
    private String cachePrefix;

    /**
     * 缓存更新时通知其他节点的topic名称
     */
    private String topic = "cache:caffeine:topic";

    private Cache<Object, Object> caffeineCache;
    private Map<String, ReentrantLock> keyLockMap = new ConcurrentHashMap<String, ReentrantLock>();
    private RedisTemplate<Object, Object> stringKeyRedisTemplate;

    public CaffeineCache(String name,
                         RedisTemplate<Object, Object> stringKeyRedisTemplate,
                         String cachePrefix,
                         Cache<Object, Object> caffeineCache,
                         boolean allowNullValues) {

        super(allowNullValues);
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.cachePrefix = cachePrefix;
        this.stringKeyRedisTemplate = stringKeyRedisTemplate;
    }

    @Override
    protected Object lookup(Object key) {
        Object cacheKey = getKey(key);
        Object value = caffeineCache.getIfPresent(cacheKey);
        if (value != null) {
            logger.debug("get cache from caffeine, the key is : {}", cacheKey);
            return value;
        }
        return value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }

        ReentrantLock lock = keyLockMap.get(key.toString());
        if (lock == null) {
            logger.debug("create lock for key : {}", getKey(key));
            lock = new ReentrantLock();
            ReentrantLock getLock = keyLockMap.putIfAbsent(key.toString(), lock);
            if(getLock != null){
                lock = getLock;
            }
        }

        try {
            lock.lock();
            value = lookup(key);
            if (value != null) {
                return (T) value;
            }
            value = valueLoader.call();
            Object storeValue = toStoreValue(value);
            put(key, storeValue);
            return (T) value;
        } catch (Exception e) {
            /*if (e.getCause() instanceof BusinessException) {
                BusinessException businessException = (BusinessException) e.getCause();
                throw businessException;
            }*/
            throw new ValueRetrievalException(key, valueLoader, e.getCause());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(Object key, Object value) {
        Object cacheKey = getKey(key);
        if (!super.isAllowNullValues() && value == null) {
            this.evict(cacheKey);
            return;
        }
        caffeineCache.put(cacheKey, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object cacheKey = getKey(key);
        Object prevValue = null;
        synchronized (key) {
            prevValue = lookup(cacheKey);
            if (prevValue == null) {
                caffeineCache.put(cacheKey, toStoreValue(value));
            }
        }
        return toValueWrapper(prevValue);
    }

    /**
     * 清除caffeine中的缓存
     */
    @Override
    public void evict(Object key) {
        Object cacheKey = getKey(key);
        //通知其他集群清楚本地缓存
        push(new CacheMessage(this.name, cacheKey));
        caffeineCache.invalidate(cacheKey);
    }

    /**
     * 清除caffeine中的所有缓存
     */
    @Override
    public void clear() {

        //通知其他集群清楚本地缓存
        push(new CacheMessage(this.name, null));
        caffeineCache.invalidateAll();
    }

    private String getKey(Object key) {
        return StringUtils.isEmpty(cachePrefix) ? this.name.concat(":").concat(key.toString()) : cachePrefix.concat(":").concat(this.name).concat(":").concat(key.toString());
    }

    /**
     * @param message
     * @description 缓存变更时通知其他节点清理本地缓存
     */
    public void push(CacheMessage message) {
        //stringKeyRedisTemplate.convertAndSend(topic, message);
    }

    /**
     * @param key
     * @description 清理本地缓存
     */
    public void clearLocal(Object key) {
        logger.debug("clear local cache, the key is : {}", key);
        if (key == null || StringUtils.isEmpty(key.toString())) {
            logger.debug("clear invalidateAll");
            caffeineCache.invalidateAll();
        } else {
            caffeineCache.invalidate(key);
        }
    }

    public ConcurrentMap<Object, Object> asMap() {
        return caffeineCache.asMap();
    }

    public CacheStats stats() {
        return caffeineCache.stats();
    }

    public long estimatedSize() {
        return caffeineCache.estimatedSize();
    }
}
