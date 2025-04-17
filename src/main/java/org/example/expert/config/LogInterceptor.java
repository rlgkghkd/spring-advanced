package org.example.expert.config;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String bearerJwt = request.getHeader("Authorization");
        String jwt = jwtUtil.substringToken(bearerJwt);
        Claims claims = jwtUtil.extractClaims(jwt);
        if (!claims.get("userRole", String.class).equals("ADMIN")){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}

        log.info("Requested time : [{}], Request URL : [{}]", new Date(), request.getRequestURI());
        return true;
    }
}
