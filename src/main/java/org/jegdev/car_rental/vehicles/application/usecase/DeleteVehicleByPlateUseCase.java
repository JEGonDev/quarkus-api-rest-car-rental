package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

import java.util.Optional;

@ApplicationScoped
public class DeleteVehicleByPlateUseCase {

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
        validateIfPlateExists(plate); // Verifica si la placa existe antes de eliminar
        removeVehicle(plate); // Elimina el vehículo del repositorio
    }

    /**
     * Valida si existe un vehículo con la placa especificada.
     *
     * @param plate La placa del vehículo a validar.
     * @throws VehicleNotFoundByPlateException si no se encuentra un vehículo con esa placa.
     */
    private void validateIfPlateExists(String plate) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findByPlate(plate);

        if (existingVehicle.isEmpty()) {
            throw new VehicleNotFoundByPlateException(plate); // Lanza excepción si no se encuentra el vehículo
        }
    }

    /**
     * Elimina un vehículo del repositorio por su placa.
     *
     * @param plate La placa del vehículo a eliminar.
     */
    private void removeVehicle(String plate) {
        vehicleRepository.deleteByPlate(plate); // Elimina el vehículo del repositorio
    }
}
