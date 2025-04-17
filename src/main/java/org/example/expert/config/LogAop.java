package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAop {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Pointcut("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    private void cut(){}

    @Around("cut()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws  Throwable{
        Object [] args = joinPoint.getArgs();
        //HttpRequest 가 오는 경우, RequestContextListener.requestInitialized(ServletRequestEvent requestEvent) 함수에 의해 값이 전달되기 때문에 값을 받을 수 있다
        //날 죽여라
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        String bearerJwt = request.getHeader("Authorization");
        String jwt = jwtUtil.substringToken(bearerJwt);
        Claims claims = jwtUtil.extractClaims(jwt);
        String userMail = claims.get("email", String.class);
        User user = userRepository.findByEmail(userMail).orElseThrow(() -> new InvalidRequestException("가입되지 않은 유저입니다."));

        log.info("commissioner id : {}", user.getId());
        log.info("requested URL : {}", request.getRequestURI());
        log.info("requested time : {}", new Date());
        UserRoleChangeRequest dto = (UserRoleChangeRequest) args[args.length-1];
        log.info("requested role : {}", dto.getRole());
        Object returnObj = joinPoint.proceed();

        log.info("respond time : {}", new Date());
        log.info("return type = {}", returnObj.getClass().getSimpleName());
        log.info("return value = {}", returnObj);

        return returnObj;
    }
}
