package org.example.api;

import org.example.dao.UserDao;
import org.example.dto.ApiException;
import org.example.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional(rollbackOn = ApiException.class)
public class UserApi {
    @Autowired
    private UserDao userDao;

    public UserPojo getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    public void add(UserPojo userPojo) {
        userDao.add(userPojo);
    }
}
