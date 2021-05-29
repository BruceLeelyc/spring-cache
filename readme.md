SpEl表达式
    <br/>&nbsp;&nbsp;&nbsp;&nbsp;SpEL表达式可基于上下文并通过使用缓存抽象，提供与root独享相关联的缓存特定的内置参数。

<br/>名称	位置	描述	示例
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; methodName	root对象	当前被调用的方法名	#root.methodname
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; method	root对象	当前被调用的方法	#root.method.name
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; target	root对象	当前被调用的目标对象实例	#root.target
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; targetClass	root对象	当前被调用的目标对象的类	#root.targetClass
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; args	root对象	当前被调用的方法的参数列表	#root.args[0]
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; caches	root对象	当前方法调用使用的缓存列表	#root.caches[0].name
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; Argument Name	执行上下文	当前被调用的方法的参数，如findArtisan(Artisan artisan),可以通过#artsian.id获得参数	#artsian.id
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; result	执行上下文	方法执行后的返回值（仅当方法执行后的判断有效，如 unless cacheEvict的beforeInvocation=false）	#result
<br/><br/>
@Cacheable(value = "users", key = "#user.userCode" condition = "#user.age < 35")
<br/>public User getUser(User user) {
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; System.out.println("User with id " + user.getUserId() + " requested.");
    <br/>&nbsp;&nbsp;&nbsp;&nbsp; return users.get(Integer.valueOf(user.getUserId()));
<br/>}
<br>
<br/>Question:缓存是否有过期时间,缓存数量:
<br/>//    Spring Cache @Cacheable本身不支持key expiration的设置，以下代码可自定义实现Spring Cache的expiration，针对Redis、SpringBoot2.0。
<br/>//
<br/>//    // 指明自定义cacheManager的bean name
<br/>//    @Cacheable(value = "test",key = "'obj1'",cacheManager = "customCacheManager")
<br/>//    public User cache1(){
<br/>//        User user = new User().setId(1);
<br/>//        logger.info("1");
<br/>//        return user;
<br/>//    }
<br/>//    // 自定义的cacheManager，实现存活2天
<br/>//    @Bean(name = "customCacheManager")
<br/>//    public CacheManager cacheManager(
<br/>//            RedisTemplate<?, ?> redisTemplate) {
<br/>//        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(redisTemplate.getConnectionFactory());
<br/>//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(2));
<br/>//        return new RedisCacheManager(writer, config);
<br/>//    }
<br/>//
<br/>//    // 提供默认的cacheManager，应用于全局
<br/>//    @Bean
<br/>//    @Primary
<br/>//    public CacheManager defaultCacheManager(
<br/>//            RedisTemplate<?, ?> redisTemplate) {
<br/>//        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(redisTemplate.getConnectionFactory());
<br/>//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
<br/>//        return new RedisCacheManager(writer, config);
<br/>//    }


<br/>
<br/>
<br/>&nbsp;&nbsp;&nbsp;&nbsp; /**
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 将方法的运行结果进行缓存,以后再查询相同的数据,缓存中获取,不用调用方法.
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * CacheManager:管理多个cache组件的,对缓存的真正CRUD操作在cache组件中,每一个缓存组件有自己唯一的名字
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 属性:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      cacheNames/value:指定缓存组件的名字,将缓存结果放到那个组件中,这个组件是个数组,可以指定多个缓存.
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      key:缓存数据使用的key.可以用它来指定,默认是使用方法参数的值,1-方法的返回值
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *          编写SPEL: #id=参数id的值 例:#a0, #p0, #root.args[0]
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      keyGenerator:key的生成器,可以自己指定key的生成器的组件id,key/keyGenerator:二先一使用
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      cacheManager:指定缓存管理器或者cacheResolver获取解析器
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      condition:指定符合条件的情况下才缓存,如:condition="#id>10"
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      unless:否定缓存,当unless指定的条件为true,方法的返回值就不会被缓存,可以获取结果进行判断
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      sync:是否使用异步模式
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 原理:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 自动配置类:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 绑在配置类:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      {@link org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration}
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 哪个配置生效:SimpleCacheConfiguration
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      (properties配置文件设置:debug=true,可以打印默认那个配置匹配:SimpleCacheConfiguration matched)
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      给容器注册了一个:CacheManager的ConcurrentMapCacheManager
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      可以获取和创建ConcurrentMapCache类型的缓存组件,倔的作用将数据保存在ConcurrentMap中
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      运行流程:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      @Cacheable:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      1.方法运行之前,先去查询Cache(缓存组件),按照cacheNames指定的名字获取;(cacheManager先获取相应的缓存),第一次获取缓存如果没有cache组件会自动创建
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      2.去cache中查找缓存的内容,使用一个key,默认就是方法的参数;k
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *          key是按照某种策略生成的,默认是使用keyGenerator生成的,默认使用SimpleKeyGenerator生成key
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *          simpleKeyGenerator生成key默认策略:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *              如果没有参数:key = new SimpleKey()
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *              如果有一个参数:key = 参数的值
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *              如果有多个参数:key = new SimpleKey(params)
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      3.没有查到缓存就调用目标方法
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      4.将目标方法返回的结果放进缓存中
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * @Cacheable标注的方法执行之前先来检查缓存中有没有这个数据,默认按照参数的值key去查询缓存,
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      如果没有就运行方法并将结果放入缓存,以后再查询就使用缓存中的数据直接返回
<br/>&nbsp;&nbsp;&nbsp;&nbsp; * 核心:
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      1.使用CacheManager[ConcurrentMapCacheManager]按照名字等到Cache[ConcurrentMapCache]组件
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *      2.key使用keyGenerator生成的,默认是SimpleKeyGenerator
<br/>&nbsp;&nbsp;&nbsp;&nbsp; *
<br/>&nbsp;&nbsp;&nbsp;&nbsp; */

