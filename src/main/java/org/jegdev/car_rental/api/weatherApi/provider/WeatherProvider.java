package org.jegdev.car_rental.api.weatherApi.provider;

import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;

public interface WeatherProvider {
    WeatherInfo getCurrentWeather(String locationOrCoords);
}
