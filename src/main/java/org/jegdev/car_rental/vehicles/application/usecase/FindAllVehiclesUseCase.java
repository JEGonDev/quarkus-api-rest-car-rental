package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.NoExistsVehiclesException;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;

import java.util.List;

@ApplicationScoped
public class FindAllVehiclesUseCase {

    private final VehicleRepository vehicleRepository;
    private final VehicleDtoMapper vehicleDtoMapper;

    /**
     * Constructor for FindAllVehiclesUseCase.
     *
     * @param vehicleRepository the repository to access vehicle data
     * @param vehicleDtoMapper the mapper to convert Vehicle entities to DTOs
     */
    @Inject
    public FindAllVehiclesUseCase(VehicleRepository vehicleRepository, VehicleDtoMapper vehicleDtoMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleDtoMapper = vehicleDtoMapper;
    }

    /**
     * Busca y devuelve una lista de todos los vehículos como DTOs.
     *
     * @return una lista de objetos VehicleResponse.
     * @throws NoExistsVehiclesException si no se encuentran vehículos en la base de datos.
     */
    public List<VehicleResponse> findAllVehicles() {
        List<Vehicle> vehicleList = vehicleRepository.findAll();

        if (vehicleList.isEmpty()) {
            throw new NoExistsVehiclesException();
        }

        // Se llama al mapper para convertir la lista de dominio a una lista de DTOs
        return vehicleDtoMapper.toResponseList(vehicleList);
    }
}
