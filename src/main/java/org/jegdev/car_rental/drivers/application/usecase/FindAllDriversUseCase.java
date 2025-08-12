package org.jegdev.car_rental.drivers.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.NoExistsDriversException;

import java.util.List;

/**
 * Caso de uso para obtener todos los conductores.
 * Se encarga de la lógica de negocio para recuperar la lista completa de conductores.
 */
@ApplicationScoped
public class FindAllDriversUseCase {
    private static final Logger LOG = Logger.getLogger(FindAllDriversUseCase.class.getName());

    private final DriverRepository driverRepository;

    @Inject
    public FindAllDriversUseCase(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * Encuentra y devuelve una lista de todos los conductores.
     * @return Una lista de objetos de dominio Driver.
     * @throws NoExistsDriversException si no se encuentra ningún conductor.
     */
    public List<Driver> findAllDrivers() {
        LOG.info("Iniciando caso de uso para obtener todos los conductores.");

        List<Driver> driverList = driverRepository.findAll();
        LOG.debugf("Se encontraron %d conductores en el repositorio.", driverList.size());

        // Se valida que la lista no esté vacía
        if (driverList.isEmpty()) {
            LOG.warn("No se encontraron conductores en la base de datos. Lanzando excepción.");
            throw new NoExistsDriversException();
        }

        LOG.info("Lista de conductores obtenida exitosamente.");
        return driverList;
    }
}
