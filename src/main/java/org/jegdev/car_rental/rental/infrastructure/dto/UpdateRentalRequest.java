package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) para recibir los datos de actualización parcial de una renta desde el cliente.
 * Contiene anotaciones para la validación de los datos de entrada y documentación OpenAPI.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "UpdateRentalRequest",
        description = "Representa los datos de entrada para la actualización parcial de una renta en el sistema de alquiler de vehículos."
)
public class UpdateRentalRequest {

    @Schema(
            description = "Identificador único del conductor asociado a la renta, como el número de documento.",
            example = "1020304050",
            required = false
    )
    @Size(min = 5, max = 20, message = "El identificador del conductor debe tener entre 5 y 20 caracteres.")
    public String driverId;

    @Schema(
            description = "Identificador único del vehículo asociado a la renta, como la placa del vehículo.",
            example = "ABC123",
            required = false
    )
    @Size(min = 3, max = 10, message = "El identificador del vehículo debe tener entre 3 y 10 caracteres.")
    public String vehicleId;

    @Schema(
            description = "Fecha de inicio de la renta, en formato ISO 8601.",
            example = "2025-08-14T10:00:00Z",
            required = false
    )
    @FutureOrPresent(message = "La fecha de inicio debe ser en el presente o futuro.")
    public Instant startDate;

    @Schema(
            description = "Fecha de finalización de la renta, en formato ISO 8601.",
            example = "2025-08-20T10:00:00Z",
            required = false
    )
    @Future(message = "La fecha de fin debe ser en el futuro.")
    public Instant endDate;

    @Schema(
            description = "Ubicación de origen de la renta.",
            example = "Bogotá",
            required = false
    )
    @Size(min = 2, max = 100, message = "El origen debe tener entre 2 y 100 caracteres.")
    public String origin;

    @Schema(
            description = "Ubicación de destino de la renta.",
            example = "Medellín",
            required = false
    )
    @Size(min = 2, max = 100, message = "El destino debe tener entre 2 y 100 caracteres.")
    public String destination;

    @Schema(
            description = "Precio de la renta en moneda local.",
            example = "150000.0",
            required = false
    )
    @Min(value = 0, message = "El precio debe ser un valor positivo.")
    public Double price;

    @Schema(
            description = "Estado de la renta (e.g., PENDING, ACTIVE, COMPLETED).",
            example = "PENDING",
            required = false
    )
    public RentalStatus status;
}