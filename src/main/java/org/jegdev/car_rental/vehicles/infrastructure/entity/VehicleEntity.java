package org.jegdev.car_rental.vehicles.infrastructure.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;

// La clase VehicleEntity extiende PanacheMongoEntity para aprovechar las funcionalidades de Panache
// como la gestión de identificadores y operaciones CRUD simplificadas.
// Esta clase representa la entidad de vehículo en la base de datos MongoDB.

@Getter
@Setter
@Builder
@MongoEntity(collection = "vehicles")
public class VehicleEntity extends PanacheMongoEntity {
    private VehicleType type; // CAR, MOTO, TRUCK
    private String brand; // Marca del vehículo
    private String model; // Modelo del vehículo
    private String plate; // Matrícula del vehículo
    private int year; // Anio de fabricación del vehículo
    private VehicleStatus status; // AVAILABLE, RENTED, MAINTENANCE
    private double dailyRate; // Tarifa por día en la moneda local
}
