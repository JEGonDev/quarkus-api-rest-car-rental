package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;

import java.util.Optional;

/**
 * Caso de uso para obtener el ID de una renta a través de la placa de un vehículo.
 * Orquesta la lógica para buscar una renta asociada a una placa de vehículo y devolver su ID.
 */
@ApplicationScoped
public class GetRentalIdByVehiclePlateUseCase {

    private static final Logger LOG = Logger.getLogger(GetRentalIdByVehiclePlateUseCase.class.getName());

    private final RentalRepository rentalRepository;

    @Inject
    public GetRentalIdByVehiclePlateUseCase(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * Obtiene el ID de una renta buscando por la placa del vehículo asociado.
     *
     * @param vehiclePlate La placa del vehículo asociada a la renta.
     * @return El ID de la renta encontrada.
     * @throws RentalNotFoundException Si no se encuentra una renta para la placa dada.
     */
    public String getRentalIdByVehiclePlate(String vehiclePlate) {
        LOG.infof("Iniciando búsqueda de renta para la placa de vehículo: %s", vehiclePlate);

        // Paso 1: Buscar y validar la existencia de la renta
        Rental rental = findRentalOrThrow(vehiclePlate);

        LOG.infof("Renta encontrada para la placa de vehículo: %s, ID: %s", vehiclePlate, rental.getId());
        return rental.getId();
    }

    /**
     * Busca una renta por la placa del vehículo y lanza una excepción si no se encuentra.
     *
     * @param vehiclePlate La placa del vehículo a buscar.
     * @return El objeto de dominio Rental encontrado.
     * @throws RentalNotFoundException Si no se encuentra una renta asociada.
     */
    private Rental findRentalOrThrow(String vehiclePlate) {
        LOG.debugf("Buscando renta asociada a la placa de vehículo: %s", vehiclePlate);
        Optional<Rental> rentalOptional = rentalRepository.findByVehicleId(vehiclePlate);

        if (rentalOptional.isPresent()) {
            LOG.debugf("Renta encontrada para la placa de vehículo: %s", vehiclePlate);
            return rentalOptional.get();
        } else {
            LOG.warnf("No se encontró renta para la placa de vehículo: %s. Lanzando excepción", vehiclePlate);
            throw new RentalNotFoundException("No se encontró renta para la placa de vehículo: " + vehiclePlate);
        }
    }
}