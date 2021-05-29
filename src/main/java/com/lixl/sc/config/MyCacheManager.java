package com.lixl.sc.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @ClassName: MyCacheManager
 * @Description:
 * @Author: lixl
 * @Date: 2021/5/29 17:34
 */
@Configuration
public class MyCacheManager {

    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator() {
        KeyGenerator keyGenerator = new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                return method.getName() + "[" + Arrays.asList(params).toString() + "]";
            }
        };
        return keyGenerator;
    }
}