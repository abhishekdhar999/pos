package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.api.UserApi;
import org.example.config.ApplicationProperties;
import org.example.dto.ApiException;
import org.example.models.form.LoginForm;
import org.example.pojo.UserPojo;
import org.example.utils.LoginResponse;
import org.example.utils.SecurityUtil;
import org.example.utils.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Objects;
@Api
@RestController
public class LoginController {
    @Autowired
    private UserApi userApi;
    @Autowired
    private ApplicationProperties properties;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @ApiOperation("login a user")
    @RequestMapping(path = "/session/login", method = RequestMethod.POST)
    public LoginResponse login(HttpServletRequest request, @RequestBody LoginForm loginForm) throws ApiException {
        UserPojo userPojo = userApi.getByEmail(loginForm.getEmail());
        if (Objects.isNull(userPojo)) {
            throw new ApiException("Username or password is invalid.");
        }
        // Create authentication object
        Authentication authentication = convert(userPojo, properties.getSupervisorEmail());
        // Create new session
        HttpSession session = request.getSession(true);
        // Attach Spring SecurityContext to this new session
        SecurityUtil.createContext(session);
        // Attach Authentication object to the Security Context
        SecurityUtil.setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return new LoginResponse(principal.getEmail(), principal.getRole());
    }

    @RequestMapping(path = "/session/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
    }

    private static Authentication convert(UserPojo p, String supervisor) {
        // Create principal
        System.out.println("email"+ p.getEmail());
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(p.getEmail());
        principal.setId(p.getId());

        // Create Authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        if(supervisor.equals(p.getEmail())){
            authorities.add(new SimpleGrantedAuthority("supervisor"));
            principal.setRole("supervisor");
        } else{
            authorities.add(new SimpleGrantedAuthority("operator"));
            principal.setRole("operator");
        }

        // Create Authentication
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        return token;
    }
}
