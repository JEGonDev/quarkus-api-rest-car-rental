package org.jegdev.car_rental.drivers.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
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
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Driver save(Driver driver) {
        DriverEntity driverEntity = mapper.toEntity(driver);
        repository.persist(driverEntity);
        return mapper.toDomain(driverEntity);
    }

    /**
     * Busca un conductor por su identificador de documento.
     * @param documentId El ID del documento del conductor.
     * @return Un Optional que contiene el conductor si se encuentra, o un Optional vacío si no.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Optional<Driver> findByDocumentId(String documentId) {
        return repository.find("documentId", documentId)
                .firstResultOptional()
                .map(mapper::toDomain);
    }

    /**
     * Obtiene una lista de todos los conductores registrados.
     * @return Una lista de objetos Driver.
     */
    @Override
    public List<Driver> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Actualiza un conductor existente.
     * @param driver El objeto de dominio Driver con los datos actualizados.
     * @return El conductor actualizado.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public Driver update(Driver driver) {
        DriverEntity driverEntity = mapper.toEntity(driver);
        repository.update(driverEntity);
        return mapper.toDomain(driverEntity);
    }

    /**
     * Elimina un conductor por su identificador de documento.
     * @param documentId El ID del documento del conductor a eliminar.
     */
    @Override
    @Retry(maxRetries = 3, delay = 200)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
    public void deleteByDocumentId(String documentId) {
        repository.delete("documentId", documentId);
    }
}
