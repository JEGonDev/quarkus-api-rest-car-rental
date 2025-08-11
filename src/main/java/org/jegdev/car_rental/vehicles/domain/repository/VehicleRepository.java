package org.jegdev.car_rental.vehicles.domain.repository;

import org.jegdev.car_rental.vehicles.domain.model.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findByPlate(String plate);
    void deleteByPlate(String plate);
    List<Vehicle> findAll();
}
