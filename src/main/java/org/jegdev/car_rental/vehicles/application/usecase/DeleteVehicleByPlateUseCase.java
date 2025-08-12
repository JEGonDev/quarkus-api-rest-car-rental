package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

import java.util.Optional;

@ApplicationScoped
public class DeleteVehicleByPlateUseCase {

    private static final Logger LOG = Logger.getLogger(DeleteVehicleByPlateUseCase.class.getName());

    private final VehicleRepository vehicleRepository;

    @Inject
    public DeleteVehicleByPlateUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Elimina un vehículo del repositorio por su placa.
     *
     * @param plate La placa del vehículo a eliminar.
     * @throws VehicleNotFoundByPlateException si no se encuentra un vehículo con la placa especificada.
     */
    public void deleteVehicleByPlate(String plate) {
        LOG.infof("Iniciando eliminación del vehículo con placa: %s", plate);

        validateIfPlateExists(plate); // Verifica si la placa existe antes de eliminar
        removeVehicle(plate); // Elimina el vehículo del repositorio

        LOG.infof("Vehículo con placa: %s eliminado exitosamente.", plate);
    }

    /**
     * Valida si existe un vehículo con la placa especificada.
     *
     * @param plate La placa del vehículo a validar.
     * @throws VehicleNotFoundByPlateException si no se encuentra un vehículo con esa placa.
     */
    private void validateIfPlateExists(String plate) {
        LOG.debugf("Validando la existencia del vehículo con placa: %s", plate);
        Optional<Vehicle> existingVehicle = vehicleRepository.findByPlate(plate);

        if (existingVehicle.isEmpty()) {
            LOG.warnf("No se encontró un vehículo con la placa: %s. Lanzando excepción.", plate);
            throw new VehicleNotFoundByPlateException(plate); // Lanza excepción si no se encuentra el vehículo
        }
    }

    /**
     * Elimina un vehículo del repositorio por su placa.
     *
     * @param plate La placa del vehículo a eliminar.
     */
    private void removeVehicle(String plate) {
        LOG.debugf("Eliminando el vehículo con placa: %s del repositorio.", plate);
        vehicleRepository.deleteByPlate(plate); // Elimina el vehículo del repositorio
    }
}
