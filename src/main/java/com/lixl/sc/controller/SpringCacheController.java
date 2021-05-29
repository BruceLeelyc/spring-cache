package com.lixl.sc.controller;

import com.lixl.sc.POJO.User;
import com.lixl.sc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SpringCacheController
 * @Description:
 * @Author: lixl
 * @Date: 2021/5/25 11:27
 */
@RestController
@RequestMapping("spring/cache")
public class SpringCacheController {

    private static final Logger logger = LoggerFactory.getLogger(SpringCacheController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/findUserById")
    public Object findUserById(Long userId) {
        User user = userService.findUserById(userId);
        return user;
    }
}
