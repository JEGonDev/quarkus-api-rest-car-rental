package org.jegdev.car_rental.vehicles.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;

@Data
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private String id; // Identificador único del vehículo
    private VehicleType type; // CAR, MOTO, TRUCK
    private String brand; // Marca del vehículo
    private String model; // Modelo del vehículo
    private String plate; // Matrícula del vehículo
    private int year; // Anio de fabricación del vehículo
    private VehicleStatus status; // AVAILABLE, RENTED, MAINTENANCE
    private double dailyRate; // Tarifa por día en la moneda local
}
