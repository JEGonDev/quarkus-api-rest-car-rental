package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.infrastructure.entity.VehicleEntity;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehiclePersistenceMapper;

import java.util.List;
import java.util.Optional;

public class VehicleRepositoryImpl implements VehicleRepository {

    // Inyectamos PanacheMongoRepository para realizar operaciones CRUD sobre la entidad VehicleEntity.
    // PanacheMongoRepository proporciona métodos predefinidos para interactuar con MongoDB de manera sencilla y eficiente.
    private final PanacheMongoRepository<VehicleEntity> repository;
    private final VehiclePersistenceMapper vehiclePersistenceMapper;

    // La clase VehicleRepositoryImpl implementa la interfaz VehicleRepository
    // y utiliza PanacheMongoRepository para realizar operaciones CRUD sobre la entidad VehicleEntity.
    @Inject
    public VehicleRepositoryImpl(PanacheMongoRepository<VehicleEntity> repository, VehiclePersistenceMapper vehiclePersistenceMapper) {
        this.repository = repository;
        this.vehiclePersistenceMapper = vehiclePersistenceMapper;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return null;
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return repository.find("plate", plate)
                .firstResultOptional()
                .map(vehiclePersistenceMapper::toDomain);
    }

    @Override
    public void deleteByPlate(String plate) {

    }

    @Override
    public List<Vehicle> findAll() {
        return List.of();
    }
}
