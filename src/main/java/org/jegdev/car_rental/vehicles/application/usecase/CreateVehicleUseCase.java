package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleAlreadyExistsException;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleRequest;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;

import java.util.Optional;

@ApplicationScoped // Se crea una instancia unica para todo el ciclo de vida de la App
public class CreateVehicleUseCase {

    private final static Logger LOG = Logger.getLogger(CreateVehicleUseCase.class.getName());

    private final VehicleRepository vehicleRepository; // Repositorio para acceder a los vehículos
    private final VehicleDtoMapper vehicleDtoMapper; // Mapper para convertir DTO a dominio

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param vehicleRepository Repositorio de vehículos para acceder a los datos.
     */
    @Inject
    public CreateVehicleUseCase(VehicleRepository vehicleRepository, VehicleDtoMapper vehicleDtoMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleDtoMapper = vehicleDtoMapper;
    }

    /**
     * Método principal para crear un nuevo vehículo.
     * Orquesta los pasos necesarios: validación, mapeo y guardado.
     *
     * @param vehicleRequest El DTO con los datos del vehículo a crear.
     * @return La entidad de dominio del vehículo creado.
     */
    public Vehicle createVehicle(VehicleRequest vehicleRequest) {
        LOG.infof("Iniciando caso de uso para crear un nuevo vehículo con placa: %s", vehicleRequest.getPlate());

        // Paso 1: Validar que la placa no exista
        validatePlateDoesNotExist(vehicleRequest.getPlate());

        // Paso 2: Mapear el DTO a la entidad de dominio
        Vehicle vehicleToSave = mapToDomain(vehicleRequest);

        // Paso 3: Guardar el vehículo en la base de datos
        Vehicle savedVehicle = saveVehicle(vehicleToSave);

        LOG.infof("Vehículo con placa: %s creado exitosamente con ID: %s", savedVehicle.getPlate(), savedVehicle.getId());
        return savedVehicle;
    }

    /**
     * Valida que no exista un vehículo con la misma placa.
     * Si ya existe, lanza una excepción personalizada.
     *
     * @param plate La placa del vehículo a validar.
     * @throws VehicleAlreadyExistsException si ya existe un vehículo con esa placa.
     */
    private void validatePlateDoesNotExist(String plate) {
        LOG.debugf("Validando si la placa %s ya existe en el sistema.", plate);
        Optional<Vehicle> existingVehicle = vehicleRepository.findByPlate(plate);
        if (existingVehicle.isPresent()) {
            LOG.warnf("La placa %s ya existe. Lanzando excepción de VehicleAlreadyExistsException.", plate);
            throw new VehicleAlreadyExistsException(plate);
        }
        LOG.debugf("La placa %s está disponible.", plate);
    }

    /**
     * Mapea el DTO de solicitud a la entidad de dominio Vehicle.
     *
     * @param vehicleRequest El DTO con los datos del vehículo.
     * @return La entidad de dominio Vehicle.
     */
    private Vehicle mapToDomain(VehicleRequest vehicleRequest) {
        LOG.debugf("Mapeando el DTO de solicitud a una entidad de dominio.");
        return vehicleDtoMapper.toDomain(vehicleRequest);
    }

    /**
     * Guarda el vehículo en la base de datos utilizando el repositorio.
     *
     * @param vehicle La entidad de dominio del vehículo a guardar.
     * @return El vehículo guardado con su ID asignado.
     */
    private Vehicle saveVehicle(Vehicle vehicle) {
        LOG.debugf("Guardando el vehículo en el repositorio.");
        return vehicleRepository.save(vehicle);
    }
}
