package com.lixl.sc.service;

import com.lixl.sc.POJO.User;
import com.lixl.sc.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @ClassName: UserServiceImpl
 * @Description:
 * @Author: lixl
 * @Date: 2021/5/29 10:55
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 将方法的运行结果进行缓存,以后再查询相同的数据,缓存中获取,不用调用方法.
     * CacheManager:管理多个cache组件的,对缓存的真正CRUD操作在cache组件中,每一个缓存组件有自己唯一的名字
     * 属性:
     *      cacheNames/value:指定缓存组件的名字,将缓存结果放到那个组件中,这个组件是个数组,可以指定多个缓存.
     *      key:缓存数据使用的key.可以用它来指定,默认是使用方法参数的值,1-方法的返回值
     *          编写SPEL: #id=参数id的值 例:#a0, #p0, #root.args[0]
     *      keyGenerator:key的生成器,可以自己指定key的生成器的组件id,key/keyGenerator:二先一使用
     *      cacheManager:指定缓存管理器或者cacheResolver获取解析器
     *      condition:指定符合条件的情况下才缓存,如:condition="#id>10"
     *      unless:否定缓存,当unless指定的条件为true,方法的返回值就不会被缓存,可以获取结果进行判断
     *      sync:是否使用异步模式
     *
     * 原理:
     * 自动配置类:
     *      {@link org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration}
     * 绑在配置类:
     *      {@link org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration}
     *      {@link org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration}
     * 哪个配置生效:SimpleCacheConfiguration
     *      (properties配置文件设置:debug=true,可以打印默认那个配置匹配:SimpleCacheConfiguration matched)
     *      给容器注册了一个:CacheManager的ConcurrentMapCacheManager
     *      可以获取和创建ConcurrentMapCache类型的缓存组件,倔的作用将数据保存在ConcurrentMap中
     *
     *      运行流程:
     *      @Cacheable:
     *      1.方法运行之前,先去查询Cache(缓存组件),按照cacheNames指定的名字获取;(cacheManager先获取相应的缓存),第一次获取缓存如果没有cache组件会自动创建
     *      2.去cache中查找缓存的内容,使用一个key,默认就是方法的参数;k
     *          key是按照某种策略生成的,默认是使用keyGenerator生成的,默认使用SimpleKeyGenerator生成key
     *          simpleKeyGenerator生成key默认策略:
     *              如果没有参数:key = new SimpleKey()
     *              如果有一个参数:key = 参数的值
     *              如果有多个参数:key = new SimpleKey(params)
     *      3.没有查到缓存就调用目标方法
     *      4.将目标方法返回的结果放进缓存中
     *
     * @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据,默认按照参数的值key去查询缓存,
     *      如果没有就运行方法并将结果放入缓存,以后再查询就使用缓存中的数据直接返回
     * 核心:
     *      1.使用CacheManager[ConcurrentMapCacheManager]按照名字等到Cache[ConcurrentMapCache]组件
     *      2.key使用keyGenerator生成的,默认是SimpleKeyGenerator
     *
     *
     * @param userId
     * @return
     */
    @Override
    @Cacheable(cacheNames = "user", key = "#userId")
    public User findUserById(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * #root.methodname = 方法名:findById
     * #userId 参数id
     */
    @Cacheable(cacheNames = "user", key = "#root.methodname+'['+#userId+']'")
    public User findById(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * myKeyGenerator:自定义key生成器
     * {@link com.lixl.sc.config.MyCacheManager}
     * 按指定规则生成key
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "user", keyGenerator = "myKeyGenerator")
    public User findId(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * condition:缓存条件,条件满足时添加缓存,不满足时直接查询数据库
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "user", keyGenerator = "myKeyGenerator", condition = "#a0 > 1")
    public User find(Long userId) {
        return userDao.findById(userId);
    }

    /**
     * unless:排队的条件,条件不符合时添加缓存,符合时直接查询数据库
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "user", keyGenerator = "myKeyGenerator", unless = "#a0 > 1")
    public User findUser(Long userId) {
        return userDao.findById(userId);
    }

}
