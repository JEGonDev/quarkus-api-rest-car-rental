package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        // Paso 1: Validar que el ID del documento no exista.
        validateDocumentIdDoesNotExist(driverRequest.getDocumentId());

        // Paso 2: Mapear el DTO a la entidad de dominio.
        Driver driverToSave = mapToDomain(driverRequest);

        // Paso 3: Guardar el conductor en la base de datos.
        return saveDriver(driverToSave);
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
            throw new DriverAlreadyExistsException(documentId);
        }
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
