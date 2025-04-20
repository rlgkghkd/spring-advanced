package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class WeatherClientTest {

    @Mock
    RestTemplate restTemplate;
    @Mock
    RestTemplateBuilder restTemplateBuilder;
    @InjectMocks
    WeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(restTemplateBuilder.build()).willReturn(restTemplate);
        weatherClient = new WeatherClient(restTemplateBuilder);
    }


    @Test
    @DisplayName("성공적인 요청")
    void getTodayWeather() {
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        String now =  LocalDate.now().format(formatter);
        WeatherDto[] mockWeatherData = { new WeatherDto(now, "shittiy") };
        ResponseEntity<WeatherDto[]> mockResponse = new ResponseEntity<>(mockWeatherData, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).willReturn(mockResponse);
        //when
        String res = weatherClient.getTodayWeather();
        //then
        assertEquals("shittiy", res);
    }

    @Test
    @DisplayName("HttpStatus가 OK가 아님")
    void badStatus() {
        //given
        ResponseEntity<WeatherDto[]> mockResponse = new ResponseEntity<>(new WeatherDto[0], HttpStatus.NOT_FOUND);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).willReturn(mockResponse);
        //when
        ServerException exception = assertThrows(ServerException.class, ()->weatherClient.getTodayWeather());
        //then
        assertEquals("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: 404 NOT_FOUND", exception.getMessage());
    }

    @Test
    @DisplayName("날씨 정보가 없음")
    void noWeather() {
        //given
        ResponseEntity<WeatherDto[]> mockResponse = new ResponseEntity<>(null, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).willReturn(mockResponse);
        //when
        ServerException exception = assertThrows(ServerException.class, ()->weatherClient.getTodayWeather());
        //then
        assertEquals("날씨 데이터가 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("최신 날씨 정보가 아님")
    void badDate() {
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        WeatherDto[] mockWeatherData = { new WeatherDto("notToday", "shittiy") };
        ResponseEntity<WeatherDto[]> mockResponse = new ResponseEntity<>(mockWeatherData, HttpStatus.OK);
        given(restTemplate.getForEntity(any(URI.class), eq(WeatherDto[].class))).willReturn(mockResponse);
        //when
        ServerException exception = assertThrows(ServerException.class, ()->weatherClient.getTodayWeather());
        //then
        assertEquals("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.", exception.getMessage());
    }

}