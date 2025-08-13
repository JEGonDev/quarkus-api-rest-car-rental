package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

import java.time.Instant;

/**
 * DTO para la solicitud de actualización de una renta.
 * Permite cambiar ciertos campos de forma parcial.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRentalRequest {
    public String driverId;
    public String vehicleId;
    @Future(message = "La fecha de inicio debe ser en el futuro")
    public Instant startDate;
    @Future(message = "La fecha de fin debe ser en el futuro")
    public Instant endDate;
    public String origin;
    public String destination;
    public Double price;
    public RentalStatus status;
}
