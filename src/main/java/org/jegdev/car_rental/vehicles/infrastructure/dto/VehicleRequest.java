package org.jegdev.car_rental.vehicles.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;

@Data
@AllArgsConstructor
@Builder
@Schema(name = "VehicleRequest", description = "Datos requeridos para la creación o edición de un vehículo")
public class VehicleRequest {

    // Identificador del vehículo, se genera automáticamente desde MongoDB

    @Schema(description = "Tipo de vehículo", example = "CAR", required = true)
    @NotNull(message = "El tipo de vehículo es obligatorio.")
    private VehicleType type; // CAR, MOTO, TRUCK

    @Schema(description = "Marca del vehículo", example = "Toyota", required = true)
    @NotBlank(message = "La marca del vehículo es obligatoria.")
    private String brand; // Marca del vehículo

    @Schema(description = "Modelo del vehículo", example = "Corolla", required = true)
    @NotBlank(message = "El modelo del vehículo es obligatorio.")
    private String model; // Modelo del vehículo

    @Schema(description = "Matrícula del vehículo", example = "ABC-1234", required = true)
    @NotBlank(message = "La matrícula del vehículo es obligatoria.")
    private String plate; // Matrícula del vehículo

    @Schema(description = "Año de fabricación del vehículo", example = "2020", required = true)
    private int year; // Anio de fabricación del vehículo

    @Schema(description = "Estado del vehículo", example = "AVAILABLE", required = true)
    @NotNull(message = "El estado del vehículo es obligatorio.")
    private VehicleStatus status; // AVAILABLE, RENTED, MAINTENANCE

    @Schema(description = "Tarifa por día en la moneda local", example = "50.0", required = true)
    private double dailyRate; // Tarifa por día en la moneda local
}
