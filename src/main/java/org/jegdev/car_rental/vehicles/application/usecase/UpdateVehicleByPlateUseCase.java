package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleRequest;

@ApplicationScoped
public class UpdateVehicleByPlateUseCase {

    private final VehicleRepository vehicleRepository;

    @Inject
    public UpdateVehicleByPlateUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Actualiza un vehículo en el repositorio utilizando su matrícula.
     *
     * @param plate La matrícula del vehículo a actualizar.
     * @param vehicleRequest El objeto VehicleRequest que contiene los datos actualizados del vehículo.
     * @return El vehículo actualizado.
     * @throws VehicleNotFoundByPlateException si no se encuentra un vehículo con la matrícula especificada.
     */
    public Vehicle updateVehicleByPlate(String plate, VehicleRequest vehicleRequest) {
        // 1. Encuentra el vehículo existente. Lanza una excepción si no existe.
        Vehicle existingVehicle = findExistingVehicle(plate);

        // 2. Actualiza los campos del vehículo existente con los datos del request.
        updateVehicleFields(existingVehicle, vehicleRequest);

        // 3. Guarda el vehículo modificado en el repositorio.
        return vehicleRepository.update(existingVehicle);
    }

    /**
     * Busca un vehículo por su matrícula.
     *
     * @param plate La matrícula del vehículo a buscar.
     * @return El vehículo si existe.
     * @throws VehicleNotFoundByPlateException si no se encuentra un vehículo con esa matrícula.
     */
    private Vehicle findExistingVehicle(String plate) {
        return vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new VehicleNotFoundByPlateException(plate));
    }

    /**
     * Actualiza los campos del vehículo existente con los datos del DTO de entrada.
     *
     * @param vehicle El vehículo existente a modificar.
     * @param vehicleRequest El DTO con los nuevos datos.
     */
    private void updateVehicleFields(Vehicle vehicle, VehicleRequest vehicleRequest) {
        // Se actualizan solo los campos que pueden ser modificados.
        vehicle.setType(vehicleRequest.getType());
        vehicle.setBrand(vehicleRequest.getBrand());
        vehicle.setModel(vehicleRequest.getModel());
        vehicle.setYear(vehicleRequest.getYear());
        vehicle.setDailyRate(vehicleRequest.getDailyRate());
        vehicle.setPlate(vehicleRequest.getPlate());
        vehicle.setStatus(vehicleRequest.getStatus());
    }
}
