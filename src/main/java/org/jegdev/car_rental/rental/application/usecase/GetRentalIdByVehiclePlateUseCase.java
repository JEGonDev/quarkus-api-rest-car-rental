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
     * Obtiene el ID de una renta buscando por la placa del vehículo.
     *
     * @param vehiclePlate La placa del vehículo asociada a la renta.
     * @return El ID de la renta.
     * @throws RentalNotFoundException si no se encuentra una renta para la placa dada.
     */
    public String getRentalIdByVehiclePlate(String vehiclePlate) {
        LOG.infof("Buscando ID de renta por placa de vehículo: %s", vehiclePlate);

        // Busca y valida la existencia de la renta
        Rental rental = findRentalOrThrow(vehiclePlate);

        // Devuelve el ID de la renta
        LOG.infof("Renta encontrada para la placa %s con ID: %s", vehiclePlate, rental.getId());
        return rental.getId();
    }

    /**
     * Busca una renta por la placa del vehículo y lanza una excepción si no la encuentra.
     *
     * @param vehiclePlate La placa del vehículo.
     * @return La entidad Rental encontrada.
     * @throws RentalNotFoundException si la renta no existe.
     */
    private Rental findRentalOrThrow(String vehiclePlate) {
        return rentalRepository.findByVehicleId(vehiclePlate)
                .orElseThrow(() -> {
                    LOG.warnf("No se encontró renta para la placa de vehículo: %s", vehiclePlate);
                    return new RentalNotFoundException("No se encontró renta para la placa de vehículo: " + vehiclePlate);
                });
    }
}
