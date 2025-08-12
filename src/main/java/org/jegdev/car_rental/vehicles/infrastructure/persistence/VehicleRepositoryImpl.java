package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import com.mongodb.MongoWriteException;
import jakarta.enterprise.context.ApplicationScoped;
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

@ApplicationScoped
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehiclePanacheRepository repository; //
    private final VehiclePersistenceMapper vehiclePersistenceMapper;

    // La clase VehicleRepositoryImpl implementa la interfaz VehicleRepository
    // y utiliza PanacheMongoRepository para realizar operaciones CRUD sobre la entidad VehicleEntity.
    @Inject
    public VehicleRepositoryImpl(VehiclePanacheRepository repository, VehiclePersistenceMapper vehiclePersistenceMapper) {
        this.repository = repository;
        this.vehiclePersistenceMapper = vehiclePersistenceMapper;
    }

    /**
     * Guarda un vehículo en la base de datos.
     * Utiliza PanacheMongoRepository para persistir la entidad VehicleEntity.
     *
     * @param vehicle El vehículo del dominio a guardar.
     * @return El vehículo guardado convertido de vuelta al dominio.
     */
    @Retry(maxRetries = 3, delay = 200) // Reintenta la operación hasta 3 veces en caso de fallo con un retraso de 200 ms entre intentos
    @Timeout(200) // Tiempo máximo de espera de 200 ms para la operación
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000) // Abre el circuito si el 75% de las últimas 4 llamadas fallan, con un retraso de 1 segundo antes de intentar cerrar el circuito
    @Override
    public Vehicle save(Vehicle vehicle) {
        // Mapeamos el vehículo del dominio a la entidad de persistencia
        VehicleEntity vehicleEntity = vehiclePersistenceMapper.toEntity(vehicle);
        // Guardamos la entidad en la base de datos usando PanacheMongoRepository
        repository.persist(vehicleEntity);
        // Devolvemos el vehículo mapeado de vuelta al dominio
        return vehiclePersistenceMapper.toDomain(vehicleEntity);
    }

    /**
     * Busca un vehículo por su matrícula.
     * Utiliza PanacheMongoRepository para realizar la consulta.
     *
     * @param plate La matrícula del vehículo a buscar.
     * @return Un Optional que contiene el vehículo encontrado, o vacío si no se encuentra.
     */
    @Retry(maxRetries = 3, delay = 200) // Reintenta la operación hasta 3 veces en caso de fallo con un retraso de 200 ms entre intentos
    @Timeout(200) // Tiempo máximo de espera de 200 ms para la operación
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000, skipOn = MongoWriteException.class) // Abre el circuito si el 75% de las últimas 4 llamadas fallan, con un retraso de 1 segundo antes de intentar cerrar el circuito
    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return repository.find("plate", plate)
                .firstResultOptional()
                .map(vehiclePersistenceMapper::toDomain);
    }

    @Override
    public void deleteByPlate(String plate) {

    }

    /**
     * Busca todos los vehículos en la base de datos.
     * Utiliza PanacheMongoRepository para obtener una lista de todas las entidades VehicleEntity.
     *
     * @return Una lista de vehículos del dominio.
     */
    @Override
    public List<Vehicle> findAll() {
        return repository.findAll()
                .stream()
                .map(vehiclePersistenceMapper::toDomain) // Mapea cada entidad a un objeto del dominio
                .toList(); // Convierte el Stream a una lista
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        return null;
    }
}
