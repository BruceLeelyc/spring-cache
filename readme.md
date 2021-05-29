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

