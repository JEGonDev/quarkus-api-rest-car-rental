package org.jegdev.car_rental.api.weatherApi.exception;

public class WeatherProviderException extends RuntimeException{
    public WeatherProviderException(String msg, Throwable t) { super(msg, t); }
}
