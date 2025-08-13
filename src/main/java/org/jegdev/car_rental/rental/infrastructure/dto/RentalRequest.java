package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalRequest {
    @NotBlank(message = "El vehicleId no puede estar vacío")
    private String vehicleId;
    @NotBlank(message = "El driverId no puede estar vacío")
    private String driverId;
    @NotNull(message = "La fecha de inicio no puede ser nula")
    @FutureOrPresent(message = "La fecha de inicio debe ser en el presente o futuro")
    private Instant startDate;
    @NotNull(message = "La fecha de fin no puede ser nula")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private Instant endDate;
    @NotBlank(message = "El origen no puede estar vacío")
    private String origin;
    @NotBlank(message = "El destino no puede estar vacío")
    private String destination;
    @Min(value = 0, message = "El precio debe ser un valor positivo")
    private double price;
}
