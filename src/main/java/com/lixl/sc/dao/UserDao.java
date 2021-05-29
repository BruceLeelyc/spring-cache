package com.lixl.sc.dao;

import com.lixl.sc.POJO.User;

public interface UserDao {
    int deleteByPrimaryKey(Long userId);

    int insert(User record);

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User findById(Long userId);
}