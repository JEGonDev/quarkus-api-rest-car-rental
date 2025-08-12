package org.jegdev.car_rental.drivers.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) para enviar la información de un conductor al cliente.
 * Representa la respuesta de la API.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private String id;
    private String name;
    private String documentId;
    private String phoneNumber;
    private String email;
}
