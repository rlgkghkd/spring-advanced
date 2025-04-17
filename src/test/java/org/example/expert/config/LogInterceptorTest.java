package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest

class LogInterceptorTest {

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @InjectMocks
    LogInterceptor logInterceptor;
    @Mock
    JwtUtil jwtUtil;

    @Test
    public void 검증되지_않은_유저() {

        String bearerJwt = "";
        String jwt = jwtUtil.substringToken(bearerJwt);
        Claims claims = jwtUtil.extractClaims(jwt);

        //given
        Claims instead = Jwts.claims();
        instead.put("userRole", "USER");
        given(jwtUtil.extractClaims(jwt)).willReturn(instead);
        //when

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            logInterceptor.preHandle(request, response, new Object());
        });

        //then
        assertEquals("401 UNAUTHORIZED", exception.getMessage());
    }
}