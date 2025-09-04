package org.example.dto;

import org.example.api.UserApi;
import org.example.models.form.UserForm;
import org.example.pojo.UserPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class UserDto {
    @Autowired
    private UserApi userApi;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public void registerUser(UserForm userForm){
        UserPojo userPojo = new UserPojo();
        userPojo.setEmail(userForm.getEmail().toLowerCase());
        userPojo.setPassword(bCryptPasswordEncoder.encode(userForm.getPassword()));
        userApi.add(userPojo);
    }
}
