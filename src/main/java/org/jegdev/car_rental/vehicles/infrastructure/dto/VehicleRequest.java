package org.jegdev.car_rental.vehicles.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;

/**
 * DTO para la creación y actualización de un vehículo.
 * Este objeto contiene todos los campos necesarios para representar un vehículo
 * en las solicitudes de la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "VehicleRequest",
        description = "Representa los datos de entrada para la creación o actualización de un vehículo."
)
public class VehicleRequest {

    @Schema(
            description = "El tipo de vehículo. Los valores permitidos son: CAR, MOTO, TRUCK.",
            example = "CAR",
            required = true
    )
    @NotNull(message = "El tipo de vehículo no puede ser nulo. Debe ser CAR, MOTO, o TRUCK.")
    private VehicleType type; // Tipo de vehículo (CAR, MOTO, TRUCK)

    @Schema(
            description = "La marca del vehículo. No puede estar en blanco.",
            example = "Toyota",
            required = true
    )
    @NotBlank(message = "La marca del vehículo es obligatoria y no puede estar en blanco.")
    private String brand; // Marca del vehículo

    @Schema(
            description = "El modelo del vehículo. No puede estar en blanco.",
            example = "Corolla",
            required = true
    )
    @NotBlank(message = "El modelo del vehículo es obligatorio y no puede estar en blanco.")
    private String model; // Modelo del vehículo

    @Schema(
            description = "La matrícula única del vehículo. Debe seguir un formato específico. " +
                    "Este campo es clave para identificar al vehículo.",
            example = "ABC-1234",
            required = true
    )
    @NotBlank(message = "La matrícula del vehículo es obligatoria y no puede estar en blanco.")
    private String plate; // Matrícula del vehículo

    @Schema(
            description = "El año de fabricación del vehículo.",
            example = "2020",
            required = true
    )
    @Min(value = 1900, message = "El año no puede ser anterior a 1900.")
    @Max(value = 2025, message = "El año no puede ser posterior al año actual.")
    private int year; // Año de fabricación del vehículo

    @Schema(
            description = "El estado actual del vehículo. Los valores permitidos son: AVAILABLE, RENTED, MAINTENANCE.",
            example = "AVAILABLE",
            required = true
    )
    @NotNull(message = "El estado del vehículo no puede ser nulo. Debe ser AVAILABLE, RENTED, o MAINTENANCE.")
    private VehicleStatus status; // Estado del vehículo (AVAILABLE, RENTED, MAINTENANCE)

    @Schema(
            description = "La tarifa de alquiler por día en la moneda local. Debe ser un valor positivo.",
            example = "50.0",
            required = true
    )
    @Positive(message = "La tarifa diaria debe ser un valor positivo.")
    private double dailyRate; // Tarifa por día en la moneda local
}
