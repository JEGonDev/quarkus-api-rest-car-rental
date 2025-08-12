package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverNotFoundByDocumentIdException;

import org.jboss.logging.Logger;

/**
 * Caso de uso para eliminar un conductor.
 * Esta clase encapsula la lógica de negocio para encontrar y eliminar un conductor.
 */
@ApplicationScoped
public class DeleteDriverUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteDriverUseCase.class.getName());
    private final DriverRepository driverRepository;

    @Inject
    public DeleteDriverUseCase(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Elimina un conductor de la base de datos por su ID de documento.
     *
     * @param documentId El ID de documento del conductor a eliminar.
     * @throws DriverNotFoundByDocumentIdException si el conductor con el ID de documento no existe.
     */
    public void deleteDriverByDocumentId(String documentId) {
        LOG.infof("Iniciando la eliminación del conductor con ID de documento: %s", documentId);

        LOG.debugf("Verificando la existencia del conductor con ID de documento: %s", documentId);
        boolean driverExists = driverRepository.findByDocumentId(documentId).isPresent();
        if (!driverExists) {
            LOG.warnf("Intento de eliminar un conductor que no existe con ID de documento: %s", documentId);
            throw new DriverNotFoundByDocumentIdException(documentId);
        }

        LOG.debugf("Conductor encontrado. Procediendo a eliminarlo de la base de datos.");
        driverRepository.deleteByDocumentId(documentId);

        LOG.infof("Conductor con ID de documento: %s eliminado exitosamente.", documentId);
    }
}
