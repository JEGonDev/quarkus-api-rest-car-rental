package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverAlreadyExistsException;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverDtoMapper;

import java.util.Optional;

/**
 * Caso de uso para la creación de un nuevo conductor.
 * Se encarga de orquestar la lógica de negocio para validar la existencia
 * del conductor y guardarlo en la base de datos.
 */
@ApplicationScoped
public class CreateDriverUseCase {

    private static final Logger LOG = Logger.getLogger(CreateDriverUseCase.class.getName());

    private final DriverRepository driverRepository;
    private final DriverDtoMapper driverDtoMapper;

    @Inject
    public CreateDriverUseCase(DriverRepository driverRepository, DriverDtoMapper driverDtoMapper) {
        this.driverRepository = driverRepository;
        this.driverDtoMapper = driverDtoMapper;
    }

    /**
     * Método principal para crear un nuevo conductor.
     * Orquesta los pasos necesarios: validación, mapeo y guardado.
     *
     * @param driverRequest El DTO con los datos del conductor a crear.
     * @return La entidad de dominio del conductor creado.
     */
    public Driver createDriver(DriverRequest driverRequest) {
        LOG.infof("Iniciando caso de uso para crear un nuevo conductor con documento ID: %s", driverRequest.getDocumentId());

        // Paso 1: Validar que el ID del documento no exista.
        LOG.debugf("Paso 1: Validando que el documento ID %s no exista.", driverRequest.getDocumentId());
        validateDocumentIdDoesNotExist(driverRequest.getDocumentId());

        // Paso 2: Mapear el DTO a la entidad de dominio.
        LOG.debugf("Paso 2: Mapeando DriverRequest a entidad de dominio para el documento ID: %s", driverRequest.getDocumentId());
        Driver driverToSave = mapToDomain(driverRequest);

        // Paso 3: Guardar el conductor en la base de datos.
        LOG.debugf("Paso 3: Guardando conductor con documento ID: %s en el repositorio.", driverToSave.getDocumentId());
        Driver savedDriver = saveDriver(driverToSave);

        LOG.infof("Caso de uso para crear conductor finalizado exitosamente. Conductor creado con ID: %s", savedDriver.getId());
        return savedDriver;
    }

    /**
     * Valida que no exista un conductor con el mismo ID de documento.
     * Si ya existe, lanza una excepción personalizada.
     *
     * @param documentId El ID del documento del conductor a validar.
     * @throws DriverAlreadyExistsException si ya existe un conductor con ese ID de documento.
     */
    private void validateDocumentIdDoesNotExist(String documentId) {
        Optional<Driver> existingDriver = driverRepository.findByDocumentId(documentId);
        if (existingDriver.isPresent()) {
            LOG.warnf("Intento de creación fallido: Ya existe un conductor con el documento ID: %s", documentId);
            throw new DriverAlreadyExistsException(documentId);
        }
        LOG.debugf("Validación exitosa: No existe un conductor con el documento ID: %s", documentId);
    }

    /**
     * Mapea el DTO de solicitud a la entidad de dominio Driver.
     *
     * @param driverRequest El DTO con los datos del conductor.
     * @return La entidad de dominio Driver.
     */
    private Driver mapToDomain(DriverRequest driverRequest) {
        return driverDtoMapper.toDomain(driverRequest);
    }

    /**
     * Guarda el conductor en la base de datos utilizando el repositorio.
     *
     * @param driver La entidad de dominio del conductor a guardar.
     * @return El conductor guardado con su ID asignado.
     */
    private Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }
}
