package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.NoExistsVehiclesException;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;

import java.util.List;

@ApplicationScoped
public class FindAllVehiclesUseCase {

    private static final Logger LOG = Logger.getLogger(FindAllVehiclesUseCase.class.getName());

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
        LOG.info("Iniciando la búsqueda de todos los vehículos.");

        List<Vehicle> vehicleList = vehicleRepository.findAll();

        if (vehicleList.isEmpty()) {
            LOG.warn("No se encontraron vehículos en el repositorio. Lanzando excepción.");
            throw new NoExistsVehiclesException();
        }

        LOG.debugf("Se encontraron %d vehículos.", vehicleList.size());

        // Se llama al mapper para convertir la lista de dominio a una lista de DTOs
        List<VehicleResponse> responseList = vehicleDtoMapper.toResponseList(vehicleList);

        LOG.info("Búsqueda de todos los vehículos finalizada exitosamente.");
        return responseList;
    }
}
