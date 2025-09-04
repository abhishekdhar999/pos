package org.example.utils;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static void createContext(HttpSession session) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    public static void setAuthentication(Authentication token) {
        SecurityContextHolder.getContext().setAuthentication(token);
    }

}