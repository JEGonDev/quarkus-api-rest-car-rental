package org.jegdev.car_rental.rental.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;

// DTO para la respuesta de la creación de una nueva renta.
// Contiene la renta guardada y el clima del destino, que no se persiste.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentalResponse {
    private RentalResponse rentalResponse;
    private WeatherInfo destinationWeather;
}
