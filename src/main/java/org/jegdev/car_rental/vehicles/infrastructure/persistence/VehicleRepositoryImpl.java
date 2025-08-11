package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
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
    @Retry(maxRetries = 3, delay = 200) // Reintenta la operación hasta 3 veces en caso de fallo con un retraso de 200 ms entre intentos
    @Timeout(200) // Tiempo máximo de espera de 200 ms para la operación
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000) // Abre el circuito si el 75% de las últimas 4 llamadas fallan, con un retraso de 1 segundo antes de intentar cerrar el circuito
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
