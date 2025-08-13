package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

/**
 * Data Transfer Object (DTO) para recibir los datos de una solicitud de renta desde el cliente.
 * Contiene anotaciones para la validación de los datos de entrada y documentación OpenAPI.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "RentalRequest",
        description = "Representa los datos de entrada para la creación de una renta en el sistema de alquiler de vehículos."
)
public class RentalRequest {

    @Schema(
            description = "Identificador único del vehículo asociado a la renta, como la placa del vehículo.",
            example = "ABC123",
            required = true
    )
    @NotBlank(message = "El identificador del vehículo es obligatorio y no puede estar en blanco.")
    private String vehicleId;

    @Schema(
            description = "Identificador único del conductor asociado a la renta, como el número de documento.",
            example = "1020304050",
            required = true
    )
    @NotBlank(message = "El identificador del conductor es obligatorio y no puede estar en blanco.")
    private String driverId;

    @Schema(
            description = "Fecha de inicio de la renta, en formato ISO 8601.",
            example = "2025-08-14T10:00:00Z",
            required = true
    )
    @NotNull(message = "La fecha de inicio es obligatoria y no puede ser nula.")
    @FutureOrPresent(message = "La fecha de inicio debe ser en el presente o futuro.")
    private Instant startDate;

    @Schema(
            description = "Fecha de finalización de la renta, en formato ISO 8601.",
            example = "2025-08-20T10:00:00Z",
            required = true
    )
    @NotNull(message = "La fecha de fin es obligatoria y no puede ser nula.")
    @Future(message = "La fecha de fin debe ser en el futuro.")
    private Instant endDate;

    @Schema(
            description = "Ubicación de origen de la renta.",
            example = "Bogotá",
            required = true
    )
    @NotBlank(message = "El origen es obligatorio y no puede estar en blanco.")
    private String origin;

    @Schema(
            description = "Ubicación de destino de la renta.",
            example = "Medellín",
            required = true
    )
    @NotBlank(message = "El destino es obligatorio y no puede estar en blanco.")
    private String destination;

    @Schema(
            description = "Precio de la renta en moneda local.",
            example = "150000.0",
            required = true
    )
    @Min(value = 0, message = "El precio debe ser un valor positivo.")
    private double price;
}