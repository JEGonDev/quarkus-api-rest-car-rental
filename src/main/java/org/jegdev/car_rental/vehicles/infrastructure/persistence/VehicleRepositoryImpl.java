package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import com.mongodb.MongoWriteException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.infrastructure.entity.VehicleEntity;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehiclePersistenceMapper;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VehicleRepositoryImpl implements VehicleRepository {

    private static final Logger LOG = Logger.getLogger(VehicleRepositoryImpl.class.getName());

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
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    @Override
    public Vehicle save(Vehicle vehicle) {
        LOG.infof("Iniciando la operación de guardado para el vehículo con matrícula: %s", vehicle.getPlate());
        LOG.infof("Datos del vehículo a guardar: %s", vehicle);
        // Mapeamos el vehículo del dominio a la entidad de persistencia
        VehicleEntity vehicleEntity = vehiclePersistenceMapper.toEntity(vehicle);
        // Guardamos la entidad en la base de datos usando PanacheMongoRepository
        repository.persist(vehicleEntity);
        LOG.infof("Vehículo con matrícula '%s' guardado exitosamente en la base de datos.", vehicle.getPlate());
        LOG.infof("Datos del vehículo guardado: %s", vehicleEntity);
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
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000, skipOn = MongoWriteException.class)
    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        LOG.infof("Buscando vehículo por matrícula: %s", plate);
        Optional<VehicleEntity> optionalEntity = repository.find("plate", plate)
                .firstResultOptional();

        if (optionalEntity.isPresent()) {
            LOG.debugf("Vehículo con matrícula '%s' encontrado en la base de datos.", plate);
            return optionalEntity.map(vehiclePersistenceMapper::toDomain);
        } else {
            LOG.warnf("No se encontró ningún vehículo con matrícula: %s.", plate);
            return Optional.empty();
        }
    }

    /**
     * Elimina un vehículo por su matrícula.
     * Utiliza PanacheMongoRepository para realizar la eliminación.
     *
     * @param plate La matrícula del vehículo a eliminar.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public void deleteByPlate(String plate) {
        LOG.infof("Iniciando la eliminación del vehículo con matrícula: %s", plate);
        long deletedCount = repository.delete("plate", plate);
        if (deletedCount > 0) {
            LOG.infof("Vehículo con matrícula '%s' eliminado exitosamente. Total de registros eliminados: %d", plate, deletedCount);
        } else {
            LOG.warnf("No se encontró ningún vehículo con matrícula '%s' para eliminar. No se realizaron cambios.", plate);
        }
    }

    /**
     * Busca todos los vehículos en la base de datos.
     * Utiliza PanacheMongoRepository para obtener una lista de todas las entidades VehicleEntity.
     *
     * @return Una lista de vehículos del dominio.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    public List<Vehicle> findAll() {
        LOG.info("Iniciando la búsqueda de todos los vehículos.");
        List<Vehicle> vehicles = repository.findAll()
                .stream()
                .map(vehiclePersistenceMapper::toDomain)
                .toList();
        LOG.infof("Se encontraron %d vehículos en total.", vehicles.size());
        return vehicles;
    }

    /**
     * Actualiza un vehículo en la base de datos.
     * Utiliza PanacheMongoRepository para persistir los cambios en la entidad.
     *
     * @param vehicle El vehículo del dominio con los datos actualizados.
     * @return El vehículo actualizado convertido de vuelta al dominio.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000, skipOn = MongoWriteException.class)
    public Vehicle update(Vehicle vehicle) {
        LOG.infof("Iniciando la actualización para el vehículo con matrícula: %s", vehicle.getPlate());
        LOG.infof("Datos de actualización: %s", vehicle);

        // Mapeamos el objeto de dominio con los datos actualizados a una entidad.
        VehicleEntity vehicleEntity = vehiclePersistenceMapper.toEntity(vehicle);

        // Persistimos la entidad actualizada en la base de datos.
        repository.update(vehicleEntity);
        LOG.infof("Vehículo con matrícula '%s' actualizado exitosamente con nuevos datos.", vehicle.getPlate());
        LOG.infof("Datos de actualización: %s", vehicleEntity);

        // Devolvemos el vehículo actualizado.
        return vehiclePersistenceMapper.toDomain(vehicleEntity);
    }
}
