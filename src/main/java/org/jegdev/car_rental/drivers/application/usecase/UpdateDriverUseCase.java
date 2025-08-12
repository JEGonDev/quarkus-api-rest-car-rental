package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverNotFoundByDocumentIdException;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverUpdateRequest;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverDtoMapper;

import java.util.Optional;

/**
 * Caso de uso para la actualización de un conductor existente.
 * Se encarga de la lógica de negocio para buscar un conductor por su
 * ID de documento, actualizar sus datos y guardarlos.
 */
@ApplicationScoped
public class UpdateDriverUseCase {

    private static final Logger LOG = Logger.getLogger(UpdateDriverUseCase.class.getName());

    private final DriverRepository driverRepository;
    private final DriverDtoMapper driverDtoMapper;

    @Inject
    public UpdateDriverUseCase(DriverRepository driverRepository, DriverDtoMapper driverDtoMapper) {
        this.driverRepository = driverRepository;
        this.driverDtoMapper = driverDtoMapper;
    }

    /**
     * Actualiza un conductor existente por su ID de documento.
     * Orquesta los pasos necesarios: validación, actualización y guardado.
     *
     * @param documentId El ID del documento del conductor a actualizar.
     * @param driverUpdateRequest El DTO con los nuevos datos del conductor.
     * @return El objeto de dominio del conductor actualizado.
     * @throws DriverNotFoundByDocumentIdException Si el conductor no se encuentra.
     */
    public Driver updateDriverByDocumentId(String documentId, DriverUpdateRequest driverUpdateRequest) {
        LOG.infof("Iniciando actualización del conductor con ID de documento: %s", documentId);

        // Paso 1: Buscar y validar que el conductor exista.
        Driver existingDriver = findExistingDriver(documentId);

        // Paso 2: Actualizar las propiedades del conductor existente con los nuevos datos.
        Driver updatedDriver = updateDriverData(existingDriver, driverUpdateRequest);

        // Paso 3: Guardar el conductor actualizado en la base de datos.
        Driver savedDriver = saveDriver(updatedDriver);

        LOG.infof("Conductor con ID de documento: %s actualizado exitosamente.", documentId);
        return savedDriver;
    }

    /**
     * Busca un conductor por su ID de documento y lanza una excepción si no se encuentra.
     *
     * @param documentId El ID del documento del conductor a buscar.
     * @return El objeto de dominio del conductor encontrado.
     * @throws DriverNotFoundByDocumentIdException Si el conductor no existe.
     */
    private Driver findExistingDriver(String documentId) {
        LOG.debugf("Buscando conductor con ID de documento: %s para verificar su existencia.", documentId);
        Optional<Driver> driverOptional = driverRepository.findByDocumentId(documentId);

        if (driverOptional.isPresent()) {
            return driverOptional.get();
        } else {
            LOG.warnf("No se encontró conductor con ID de documento: %s. Lanzando excepción.", documentId);
            throw new DriverNotFoundByDocumentIdException(documentId);
        }
    }

    /**
     * Actualiza las propiedades de un objeto de dominio Driver con los datos de un DTO.
     *
     * @param existingDriver El objeto de dominio a actualizar.
     * @param driverRequest El DTO con los nuevos datos.
     * @return El objeto de dominio con los datos actualizados.
     */
    private Driver updateDriverData(Driver existingDriver, DriverUpdateRequest driverRequest) {
        LOG.debugf("Mapeando DTO de actualización a objeto de dominio para el conductor con ID: %s.", existingDriver.getDocumentId());
        Driver updatedData = driverDtoMapper.toDomain(driverRequest);

        existingDriver.setName(updatedData.getName());
        existingDriver.setPhoneNumber(updatedData.getPhoneNumber());
        existingDriver.setEmail(updatedData.getEmail());

        // No actualizamos el documentId, ya que es el identificador de la operación.
        LOG.debugf("Campos del conductor con ID: %s actualizados.", existingDriver.getDocumentId());

        return existingDriver;
    }

    /**
     * Guarda el conductor actualizado en la base de datos.
     *
     * @param driver El objeto de dominio del conductor a guardar.
     * @return El conductor guardado.
     */
    private Driver saveDriver(Driver driver) {
        LOG.debugf("Guardando conductor actualizado con ID de documento: %s en el repositorio.", driver.getDocumentId());
        return driverRepository.update(driver);
    }
}
