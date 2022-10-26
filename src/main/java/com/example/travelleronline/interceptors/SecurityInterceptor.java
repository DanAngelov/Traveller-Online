package com.example.travelleronline.interceptors;

import com.example.travelleronline.exceptions.UnauthorizedException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityInterceptor implements HandlerInterceptor {

    public static final String LOGGED = "logged";
    public static final String REMOTE_ADDRESS = "remote_address";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(10*60); // 10 minutes
        }
        String uri = request.getRequestURI();
        System.out.println(uri);
        if (uri.contains("registration") ||
        uri.contains("email-verification") ||
        uri.contains("login") ||
        uri.contains("logout")) {
            return true;
        }
        String ip = request.getRemoteAddr();
        if (session.getAttribute(LOGGED) == null ||
                !(boolean) session.getAttribute(LOGGED) ||
                session.getAttribute(REMOTE_ADDRESS) == null ||
                !session.getAttribute(REMOTE_ADDRESS).equals(ip)) {
            throw new UnauthorizedException("You should log in first.");
        }
        return true;
    }

}
