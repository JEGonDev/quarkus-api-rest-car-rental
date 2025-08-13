package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

/**
 * Data Transfer Object (DTO) para recibir los datos de actualización del estado de una renta desde el cliente.
 * Contiene anotaciones para la validación de los datos de entrada y documentación OpenAPI.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "UpdateRentalStatusRequest",
        description = "Representa los datos de entrada para la actualización del estado de una renta en el sistema de alquiler de vehículos."
)
public class UpdateRentalStatusRequest {

    @Schema(
            description = "Nuevo estado de la renta (e.g., PENDING, ACTIVE, COMPLETED).",
            example = "ACTIVE",
            required = true
    )
    @NotNull(message = "El estado de la renta es obligatorio y no puede ser nulo.")
    private RentalStatus status;
}