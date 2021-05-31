package com.lixl.sc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCacheApplicationTests {

    /**
     * 1. 常见用法
     * public static void main(String... args) throws Exception {
     * Cache<String, String> cache = Caffeine.newBuilder()
     *         //5秒没有读写自动删除
     *         .expireAfterAccess(5, TimeUnit.SECONDS)
     *         //最大容量1024个，超过会自动清理空间
     *         .maximumSize(1024)
     *         .removalListener(((key, value, cause) -> {
     *             //清理通知 key,value ==> 键值对   cause ==> 清理原因
     *         }))
     *         .build();
     *
     * //添加值
     * cache.put("张三", "浙江");
     * //获取值
     * cache.getIfPresent("张三");
     * //remove
     * cache.invalidate("张三");
     * }
     *
     *
     * 2. 填充策略
     * 填充策略是指如何在key不存在的情况下，如何创建一个对象进行返回，主要分为下面四种
     * 2.1 手动(Manual)
     * public static void main(String... args) throws Exception {
     *         Cache<String, Integer> cache = Caffeine.newBuilder().build();
     *
     *         Integer age1 = cache.getIfPresent("张三");
     *         System.out.println(age1);
     *
     *         //当key不存在时，会立即创建出对象来返回，age2不会为空
     *         Integer age2 = cache.get("张三", k -> {
     *             System.out.println("k:" + k);
     *             return 18;
     *         });
     *         System.out.println(age2);
     * }
     *
     *
     * result:
     * null
     * k:张三
     * 18
     *
     * 2.2 自动(Loading)
     * public static void main(String... args) throws Exception {
     *
     *     //此时的类型是 LoadingCache 不是 Cache
     *     LoadingCache<String, Integer> cache = Caffeine.newBuilder().build(key -> {
     *         System.out.println("自动填充:" + key);
     *         return 18;
     *     });
     *
     *     Integer age1 = cache.getIfPresent("张三");
     *     System.out.println(age1);
     *
     *     // key 不存在时 会根据给定的CacheLoader自动装载进去
     *     Integer age2 = cache.get("张三");
     *     System.out.println(age2);
     * }
     *
     * result:
     * null
     * 自动填充:张三
     * 18
     *
     * 2.3 异步手动(Asynchronous Manual)
     * public static void main(String... args) throws Exception {
     *     AsyncCache<String, Integer> cache = Caffeine.newBuilder().buildAsync();
     *
     *     //会返回一个 future对象， 调用future对象的get方法会一直卡住直到得到返回，和多线程的submit一样
     *     CompletableFuture<Integer> ageFuture = cache.get("张三", name -> {
     *         System.out.println("name:" + name);
     *         return 18;
     *     });
     *
     *     Integer age = ageFuture.get();
     *     System.out.println("age:" + age);
     * }
     *
     * result:
     * name:张三
     * age:18
     *
     * 2.4 异步自动(Asynchronously Loading)
     * public static void main(String... args) throws Exception {
     *     //和1.4基本差不多
     *     AsyncLoadingCache<String, Integer> cache = Caffeine.newBuilder().buildAsync(name -> {
     *         System.out.println("name:" + name);
     *         return 18;
     *     });
     *     CompletableFuture<Integer> ageFuture = cache.get("张三");
     *
     *     Integer age = ageFuture.get();
     *     System.out.println("age:" + age);
     * }
     *
     * result:
     * name:张三
     * age:18
     *
     * 3. 驱逐策略
     * 3.1 基于容量大小
     * public static void main(String... args) throws Exception {
     *     Cache<String, Integer> cache = Caffeine.newBuilder().maximumSize(10)
     *     .removalListener((key, value, cause) -> {
     *         System.out.println(String.format("key %s was removed %s / %s", key, value, cause));
     *     }).build();
     *
     *     for (int i = 0; i < 100; i++) {
     *         cache.put("name" + i, i);
     *     }
     *     Thread.currentThread().join();
     * }
     *
     * result:
     * key name0 was removed 0 / SIZE
     * key name3 was removed 3 / SIZE
     * key name4 was removed 4 / SIZE
     * key name5 was removed 5 / SIZE
     * key name39 was removed 39 / SIZE
     * key name38 was removed 38 / SIZE
     * key name37 was removed 37 / SIZE
     * key name36 was removed 36 / SIZE
     * key name35 was removed 35 / SIZE
     * key name34 was removed 34 / SIZE
     * key name33 was removed 33 / SIZE
     * key name32 was removed 32 / SIZE
     * ...
     *
     *
     * 3.2 基于时间
     * // 写入后10秒过期,重新写入会刷新过期时间
     * Cache<String, Integer> cache1 = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
     * // 写入或读取后10秒无任务操作会过期，读写都会刷新过期时间
     * Cache<String, Integer> cache2 = Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
     *
     *
     * 参数设置:
     * initialCapacity: 初始缓存空间大小
     * maximumSize：设置缓存最大条目数，超过条目则触发回收
     * maximumWeight：设置缓存最大权重，设置权重是通过weigher方法，
     *      需要注意的是权重也是限制缓存大小的参数，并不会影响缓存淘汰策略，也不能和maximumSize方法一起使用。
     * weakKeys：将key设置为弱引用，在GC时可以直接淘汰
     * weakValues：将value设置为弱引用，在GC时可以直接淘汰
     * softValues：将value设置为软引用，在内存溢出前可以直接淘汰
     * expireAfterWrite：写入后隔段时间过期
     * expireAfterAccess：访问后隔断时间过期
     * refreshAfterWrite：写入后隔断时间刷新
     * removalListener：缓存淘汰监听器，配置监听器后，每个条目淘汰时都会调用该监听器
     * writer：writer监听器其实提供了两个监听，一个是缓存写入或更新是的write，一个是缓存淘汰时的delete，每个条目淘汰时都会调用该监听器
     *
     */

}
