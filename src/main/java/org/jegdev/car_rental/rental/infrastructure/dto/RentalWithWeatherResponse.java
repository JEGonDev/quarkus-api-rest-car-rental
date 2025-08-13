package org.jegdev.car_rental.rental.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;

/**
 * DTO para la respuesta de la consulta del estado de una renta.
 * Contiene la información de la renta y el clima del lugar de origen.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalWithWeatherResponse {
    private RentalResponse rental;
    private WeatherInfo originWeather;
}
