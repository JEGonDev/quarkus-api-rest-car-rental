package org.jegdev.car_rental.rental.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para la respuesta del servicio de clima
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    private String locationName;
    private double tempC;
    private String conditionText;
    private String iconUrl;
    private double windKph;
    private int humidity;
    private String lastUpdated;
}
