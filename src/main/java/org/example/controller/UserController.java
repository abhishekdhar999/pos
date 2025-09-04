package org.example.controller;

import org.example.dto.UserDto;
import org.example.models.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Objects;

public class UserController {

    @Autowired
    private UserDto userDto;

    @RequestMapping(method = RequestMethod.POST)
    public void registerUser(@RequestBody UserForm userForm){
        userDto.registerUser(userForm);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (!Objects.isNull(authentication) && !Objects.isNull(authentication.getPrincipal())) {
            return ResponseEntity.ok(authentication.getPrincipal()); // or custom user object
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
