package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    //mockitoExtension은 유닛테스트를 할 때 쓴다.
    //@Mock 으로 mock을 만들면 정상적으로 의존성이 주입되므로
    //MockitoAnnotations.openMocks(this);은 필요가 없다.
    //@BeforeEach
    //void setUp() {MockitoAnnotations.openMocks(this);}
    // 이건 잘못된 예시로 남겨둔다.

    private static Stream<SignupRequest> CreateSignUpRequest(){
        return Stream.of(new SignupRequest("test@mail.com", "test", "USER"), new SignupRequest("test2@mail.com", "test2", "USER"));
    }

    @ParameterizedTest
    @MethodSource("CreateSignUpRequest")
    void signup(SignupRequest request) {
        User testUser = new User("test@mail.com", "encodedPassword", UserRole.USER);
        given(passwordEncoder.encode(request.getPassword())).willReturn("test");
        given(userRepository.save(any(User.class))).willReturn(testUser);
        given(jwtUtil.createToken(any(), any(), any())).willReturn("testToken");
        SignupResponse response = authService.signup(request);

        assertEquals("testToken", response.getBearerToken());
    }

    @Test
    void dupEmail() {
        SignupRequest request = new SignupRequest("test@mail.com", "test", "USER");
        User testUser = new User("test@mail.com", "encodedPassword", UserRole.USER);

        given(userRepository.existsByEmail(anyString())).willReturn(true);
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, ()->authService.signup(request));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void signin() {
        SigninRequest request = new SigninRequest("test@mail.com", "test");
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(new User()));
        given(passwordEncoder.matches(any(),any())).willReturn(true);
        given(jwtUtil.createToken(any(), any(), any())).willReturn("testToken");
        SigninResponse response = authService.signin(request);

        assertEquals("testToken", response.getBearerToken());
    }
}