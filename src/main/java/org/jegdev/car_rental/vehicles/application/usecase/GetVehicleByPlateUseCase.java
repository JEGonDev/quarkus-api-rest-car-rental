package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

@ApplicationScoped // Se crea una instancia única de este caso de uso
public class GetVehicleByPlateUseCase {

    private final VehicleRepository vehicleRepository; // Repositorio para acceder a los vehículos

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param vehicleRepository Repositorio de vehículos para acceder a los datos.
     */
    @Inject
    public GetVehicleByPlateUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle findVehicleByPlate(String plate) {
        return vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new VehicleNotFoundByPlateException(plate)); // Lanza excepción si no se encuentra el vehículo
    }
}
