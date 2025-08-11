package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.infrastructure.entity.VehicleEntity;

import java.util.List;

public class VehicleRepositoryImpl implements VehicleRepository {

    // Inyectamos PanacheMongoRepository para realizar operaciones CRUD sobre la entidad VehicleEntity.
    // PanacheMongoRepository proporciona métodos predefinidos para interactuar con MongoDB de manera sencilla y eficiente.
    private final PanacheMongoRepository<VehicleEntity> repository;

    // La clase VehicleRepositoryImpl implementa la interfaz VehicleRepository
    // y utiliza PanacheMongoRepository para realizar operaciones CRUD sobre la entidad VehicleEntity.
    @Inject
    public VehicleRepositoryImpl(PanacheMongoRepository<VehicleEntity> repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return null;
    }

    @Override
    public Vehicle findByPlate(String plate) {
        return null;
    }

    @Override
    public void deleteByPlate(String plate) {

    }

    @Override
    public List<Vehicle> findAll() {
        return List.of();
    }
}
