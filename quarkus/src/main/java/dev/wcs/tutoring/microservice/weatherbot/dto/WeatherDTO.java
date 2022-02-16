package dev.wcs.tutoring.microservice.weatherbot.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class WeatherDTO {

    private String main;
    private BigDecimal temp;

}
