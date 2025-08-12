package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverNotFoundByDocumentIdException;

/**
 * Caso de uso para encontrar un conductor por su ID de documento.
 * Encapsula la lógica de negocio para buscar y, si no se encuentra,
 * lanzar una excepción personalizada.
 */
@ApplicationScoped // Se crea una instancia única para todo el ciclo de vida de la App
public class FindDriverByDocumentIdUseCase {

    private final DriverRepository driverRepository;

    @Inject
    public FindDriverByDocumentIdUseCase(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Busca un conductor por su ID de documento.
     *
     * @param documentId El ID del documento del conductor a buscar.
     * @return El objeto de dominio del conductor si se encuentra.
     * @throws DriverNotFoundByDocumentIdException si no se encuentra un conductor con el ID de documento especificado.
     */
    public Driver findDriverByDocumentId(String documentId) {
        return driverRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new DriverNotFoundByDocumentIdException(documentId));
    }
}
