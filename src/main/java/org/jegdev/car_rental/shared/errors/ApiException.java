package org.jegdev.car_rental.shared.errors;

import lombok.Getter;

// Excepción personalizada para manejar errores en la API.

@Getter
public class ApiException extends RuntimeException{
    private final String code;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }
}
