package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverNotFoundByDocumentIdException;

/**
 * Caso de uso para eliminar un conductor.
 * Esta clase encapsula la lógica de negocio para encontrar y eliminar un conductor.
 */
@ApplicationScoped
public class DeleteDriverUseCase {

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
        // Se busca el conductor para validar su existencia
        boolean driverExists = driverRepository.findByDocumentId(documentId).isPresent();
        if (!driverExists) {
            throw new DriverNotFoundByDocumentIdException(documentId);
        }

        // Se elimina el conductor si existe
        driverRepository.deleteByDocumentId(documentId);
    }
}
