package org.jegdev.car_rental.drivers.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.infrastructure.entity.DriverEntity;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverPersistenceMapper;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de conductores que utiliza Panache para interactuar con MongoDB.
 * Proporciona la lógica de persistencia para el módulo de conductores.
 */
@ApplicationScoped
public class DriverRepositoryImpl implements DriverRepository {

    private static final Logger LOG = Logger.getLogger(DriverRepositoryImpl.class.getName());

    private final DriverPanacheRepository repository;
    private final DriverPersistenceMapper mapper;

    @Inject
    public DriverRepositoryImpl(DriverPanacheRepository repository, DriverPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Guarda un nuevo conductor o actualiza uno existente.
     * @param driver El objeto de dominio Driver a guardar.
     * @return El conductor guardado.
     */
    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Driver save(Driver driver) {
        LOG.infof("Iniciando la operación de guardado para el conductor con documento: %s", driver.getDocumentId());
        LOG.infof("Datos del conductor a guardar: %s", driver);
        // Primero, se convierte el objeto de dominio Driver a una entidad DriverEntity
        DriverEntity driverEntity = mapper.toEntity(driver);
        // Luego, se persiste la entidad en la base de datos utilizando el repositorio Panache
        repository.persist(driverEntity);
        LOG.infof("Conductor con documento '%s' guardado exitosamente en la base de datos.", driver.getDocumentId());
        LOG.infof("Datos del conductor guardado: %s", driverEntity);
        // Finalmente, se convierte la entidad persistida de vuelta a un objeto de dominio Driver
        return mapper.toDomain(driverEntity);
    }

    /**
     * Busca un conductor por su identificador de documento.
     * @param documentId El ID del documento del conductor.
     * @return Un Optional que contiene el conductor si se encuentra, o un Optional vacío si no.
     */
    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Optional<Driver> findByDocumentId(String documentId) {
        LOG.infof("Buscando conductor por documento: %s", documentId);
        Optional<DriverEntity> optionalEntity = repository.find("documentId", documentId)
                .firstResultOptional();

        if (optionalEntity.isPresent()) {
            LOG.debugf("Conductor con documento '%s' encontrado en la base de datos.", documentId);
            return optionalEntity.map(mapper::toDomain);
        } else {
            LOG.warnf("No se encontró ningún conductor con documento: %s.", documentId);
            return Optional.empty();
        }
    }

    /**
     * Obtiene una lista de todos los conductores registrados.
     * @return Una lista de objetos Driver.
     */
    @Override
    public List<Driver> findAll() {
        LOG.info("Iniciando la búsqueda de todos los conductores.");
        List<Driver> drivers = repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
        LOG.infof("Se encontraron %d conductores en total.", drivers.size());
        return drivers;
    }

    /**
     * Actualiza un conductor existente.
     * @param driver El objeto de dominio Driver con los datos actualizados.
     * @return El conductor actualizado.
     */
    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Driver update(Driver driver) {
        LOG.infof("Iniciando la actualización para el conductor con documento: %s", driver.getDocumentId());
        LOG.infof("Datos del conductor a actualizar: %s", driver);

        DriverEntity existingEntity = repository.find("documentId", driver.getDocumentId()).firstResult();

        if (existingEntity != null) {
            LOG.debugf("Actualizando campos del conductor con documento: %s", driver.getDocumentId());
            // Solo actualizamos los campos que no son el identificador único de negocio
            existingEntity.setName(driver.getName());
            existingEntity.setPhoneNumber(driver.getPhoneNumber());
            existingEntity.setEmail(driver.getEmail());
            LOG.infof("Conductor con documento '%s' actualizado exitosamente.", driver.getDocumentId());
            LOG.infof("Datos actualizados: %s", existingEntity);
            return mapper.toDomain(existingEntity);
        } else {
            LOG.warnf("No se pudo encontrar el conductor con documento '%s' para actualizar.", driver.getDocumentId());
            // En un caso real, podrías lanzar una excepción aquí.
            return null;
        }
    }

    /**
     * Elimina un conductor por su identificador de documento.
     * @param documentId El ID del documento del conductor a eliminar.
     */
    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public void deleteByDocumentId(String documentId) {
        LOG.infof("Iniciando la eliminación del conductor con documento: %s", documentId);
        long deletedCount = repository.delete("documentId", documentId);
        if (deletedCount > 0) {
            LOG.infof("Conductor con documento '%s' eliminado exitosamente. Total de registros eliminados: %d", documentId, deletedCount);
        } else {
            LOG.warnf("No se encontró ningún conductor con documento '%s' para eliminar. No se realizaron cambios.", documentId);
        }
    }
}
