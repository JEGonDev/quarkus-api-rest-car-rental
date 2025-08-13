package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

/**
 * Caso de uso para eliminar una renta por su ID.
 */
@ApplicationScoped
public class DeleteRentalUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteRentalUseCase.class.getName());

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    @Inject
    public DeleteRentalUseCase(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Elimina una renta del sistema por su ID.
     * @param rentalId El ID de la renta a eliminar.
     * @throws RentalNotFoundException si la renta no se encuentra.
     */
    public void deleteRental(String rentalId) {
        LOG.infof("Recibida solicitud para eliminar la renta con ID: %s", rentalId);

        // Separa la lógica de validación, actualización y eliminación en métodos privados
        Rental rentalToDelete = validateRentalExists(rentalId);
        updateVehicleStatus(rentalToDelete.getVehicleId());
        removeRental(rentalId);

        LOG.infof("Renta con ID %s eliminada exitosamente. El vehículo ha sido marcado como 'AVAILABLE'.", rentalId);
    }

    /**
     * Valida la existencia de la renta.
     * @param rentalId El ID de la renta.
     * @return El objeto Rental si se encuentra.
     * @throws RentalNotFoundException si la renta no existe.
     */
    private Rental validateRentalExists(String rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> {
                    LOG.warnf("No se encontró renta para eliminación con ID: %s", rentalId);
                    return new RentalNotFoundException("No se encontró la renta para eliminación con ID: " + rentalId);
                });
    }

    /**
     * Actualiza el estado del vehículo a 'AVAILABLE' en el repositorio.
     * @param vehicleId El ID del vehículo asociado a la renta eliminada.
     */
    private void updateVehicleStatus(String vehicleId) {
        LOG.infof("Actualizando el estado del vehículo con ID: %s a 'AVAILABLE'", vehicleId);

        Vehicle vehicle = vehicleRepository.findByPlate(vehicleId)
                .orElseThrow(() -> {
                    LOG.errorf("Error fatal: No se encontró el vehículo con ID %s asociado a la renta. ", vehicleId);
                    return new VehicleNotFoundByPlateException(vehicleId);
                });

        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.update(vehicle);
    }

    /**
     * Realiza la eliminación de la renta.
     * @param rentalId El ID de la renta a eliminar.
     */
    private void removeRental(String rentalId) {
        rentalRepository.deleteByOrderId(rentalId);
    }
}
