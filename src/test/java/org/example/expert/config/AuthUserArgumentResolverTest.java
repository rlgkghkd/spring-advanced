package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class AuthUserArgumentResolverTest {

    @Mock
    MethodParameter methodParameter;
    @Mock
    ModelAndViewContainer mavContainer;
    @Mock
    NativeWebRequest webRequest;
    @Mock
    WebDataBinderFactory binderFactory;
    @InjectMocks
    private AuthUserArgumentResolver authUserArgumentResolver;

    @Mock
    HttpServletRequest request;
    @Mock
    Auth mockAuthAnnotation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Auth 어노테이션을 쓴 메서드가 AuthUser를 파라미터로 받는 케이스")
    void supportsParameter() throws NoSuchMethodException {
        //given
        given(methodParameter.getParameterAnnotation(Auth.class)).willReturn(mockAuthAnnotation);
        given(methodParameter.getParameterType()).willReturn((Class)AuthUser.class);
        //when, then
        assertTrue(authUserArgumentResolver.supportsParameter(methodParameter));
    }

    @Test
    @DisplayName("Auth 어노테이션을 쓴 메서드가 Integer를 파라미터로 받는 케이스")
    void wrongMethod() throws NoSuchMethodException {
        //given
        given(methodParameter.getParameterAnnotation(Auth.class)).willReturn(mockAuthAnnotation);
        given(methodParameter.getParameterType()).willReturn((Class)Integer.class);
        //when
        AuthException exception = assertThrows(AuthException.class, ()-> authUserArgumentResolver.supportsParameter(methodParameter));
        //then
        assertEquals("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.", exception.getMessage());
    }

    @Test
    void resolveArgument() {
        given(webRequest.getNativeRequest()).willReturn(request);
        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("email")).willReturn("mail");
        given(request.getAttribute("userRole")).willReturn("USER");
        AuthUser reUser = (AuthUser) authUserArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        assertEquals(1L, reUser.getId());
        assertEquals("mail", reUser.getEmail());
        assertEquals(UserRole.USER, reUser.getUserRole());
    }
}