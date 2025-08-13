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

import java.util.Optional;

/**
 * Caso de uso para eliminar una renta existente por su ID.
 * Orquesta la lógica de negocio para validar la existencia de la renta,
 * actualizar el estado del vehículo asociado y eliminar la renta.
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
     * Elimina una renta del sistema por su ID y actualiza el estado del vehículo asociado.
     *
     * @param rentalId El ID de la renta a eliminar.
     * @throws RentalNotFoundException Si la renta no se encuentra.
     * @throws VehicleNotFoundByPlateException Si el vehículo asociado no se encuentra.
     */
    public void deleteRental(String rentalId) {
        LOG.infof("Iniciando eliminación de la renta con ID: %s", rentalId);

        // Paso 1: Validar que la renta exista
        Rental rentalToDelete = validateRentalExists(rentalId);

        // Paso 2: Actualizar el estado del vehículo a AVAILABLE
        updateVehicleStatus(rentalToDelete.getVehicleId());

        // Paso 3: Eliminar la renta del repositorio
        removeRental(rentalId);

        LOG.infof("Renta con ID: %s eliminada exitosamente. Vehículo con placa: %s marcado como AVAILABLE",
                rentalId, rentalToDelete.getVehicleId());
    }

    /**
     * Valida la existencia de la renta por su ID.
     *
     * @param rentalId El ID de la renta a validar.
     * @return El objeto de dominio Rental si se encuentra.
     * @throws RentalNotFoundException Si la renta no existe.
     */
    private Rental validateRentalExists(String rentalId) {
        LOG.debugf("Buscando renta con ID: %s para verificar su existencia", rentalId);
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isPresent()) {
            LOG.debugf("Renta con ID: %s encontrada", rentalId);
            return rentalOptional.get();
        } else {
            LOG.warnf("No se encontró renta con ID: %s. Lanzando excepción", rentalId);
            throw new RentalNotFoundException("Renta no encontrada con ID: " + rentalId);
        }
    }

    /**
     * Actualiza el estado del vehículo asociado a AVAILABLE.
     *
     * @param vehicleId La placa del vehículo asociado a la renta.
     * @throws VehicleNotFoundByPlateException Si el vehículo no se encuentra.
     */
    private void updateVehicleStatus(String vehicleId) {
        LOG.debugf("Buscando vehículo con placa: %s para actualizar su estado a AVAILABLE", vehicleId);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByPlate(vehicleId);

        if (vehicleOptional.isEmpty()) {
            LOG.warnf("No se encontró vehículo con placa: %s. Lanzando excepción", vehicleId);
            throw new VehicleNotFoundByPlateException(vehicleId);
        }

        Vehicle vehicle = vehicleOptional.get();
        LOG.debugf("Actualizando estado del vehículo con placa: %s a AVAILABLE", vehicleId);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.update(vehicle);
    }

    /**
     * Elimina la renta del repositorio.
     *
     * @param rentalId El ID de la renta a eliminar.
     */
    private void removeRental(String rentalId) {
        LOG.debugf("Eliminando renta con ID: %s del repositorio", rentalId);
        rentalRepository.deleteByOrderId(rentalId);
    }
}