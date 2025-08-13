package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.provider.WeatherProvider;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.VehicleNotAvailableException;
import org.jegdev.car_rental.rental.infrastructure.dto.CreateRentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

import java.time.Instant;
import java.util.Optional;

// Clase que representa el caso de uso para crear una nueva renta de vehículo.
@ApplicationScoped
public class CreateRentalUseCase {

    private static final Logger LOG = Logger.getLogger(CreateRentalUseCase.class.getName());

    private final RentalRepository rentalRepository;
    private final RentalDtoMapper rentalDtoMapper;
    private final VehicleRepository vehicleRepository;
    private final WeatherProvider weatherProvider; // Proveedor de datos meteorológicos para la renta

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param rentalRepository Repositorio para la persistencia de rentas.
     * @param rentalDtoMapper Mapper para convertir DTOs a la entidad de dominio.
     * @param vehicleRepository Repositorio para consultar la disponibilidad del vehículo.
     */
    @Inject
    public CreateRentalUseCase(RentalRepository rentalRepository, RentalDtoMapper rentalDtoMapper, VehicleRepository vehicleRepository, WeatherProvider weatherProvider) {
        this.rentalRepository = rentalRepository;
        this.rentalDtoMapper = rentalDtoMapper;
        this.vehicleRepository = vehicleRepository;
        this.weatherProvider = weatherProvider;
    }

    /**
     * Método principal para orquestar la creación de una nueva renta.
     *
     * @param request El DTO con los datos de la renta a crear.
     * @return El DTO con la renta creada y el clima del destino.
     */
    public CreateRentalResponse createRental(RentalRequest request) {
        LOG.infof("Iniciando caso de uso para crear una nueva renta para el vehículo: %s", request.getVehicleId());

        // Paso 1: Validar que el vehículo exista y esté disponible
        Vehicle vehicle = validateVehicleAvailability(request.getVehicleId());

        // Paso 2: Consultar la API de clima para el destino.
        WeatherInfo destinationWeather = fetchDestinationWeather(request.getDestination());
        LOG.infof("Clima para el destino '%s' obtenido: %s", request.getDestination(), destinationWeather.locationName);

        // Paso 3: Mapear el DTO a la entidad de dominio 'Rental'.
        Rental newRental = mapToDomain(request);

        // Paso 4: Guardar la entidad 'Rental' en la base de datos.
        Rental savedRental = saveRental(newRental);

        // Paso 5: Actualizar el estado del vehículo a RENTED.
        updateVehicleStatus(vehicle);

        LOG.infof("Renta creada exitosamente con ID: %s. El vehículo con placa: %s ha sido marcado como RENTED.", savedRental.getId(), vehicle.getPlate());

        //  Mapeamos la entidad 'Rental' a 'RentalResponse'
        // antes de crear el objeto 'CreateRentalResponse'.
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(savedRental);

        // Devolvemos el DTO que agrupa ambos datos.
        return new CreateRentalResponse(rentalResponse, destinationWeather);
    }

    /**
     * Valida si el vehículo existe y está disponible para ser rentado.
     *
     * @param vehicleId Representa la placa del vehiculo.
     * @return La entidad de dominio del vehículo si está disponible.
     * @throws VehicleNotFoundByPlateException si el vehículo no existe.
     * @throws VehicleNotAvailableException si el vehículo no está disponible.
     */
    private Vehicle validateVehicleAvailability(String vehicleId) {
        LOG.debugf("Validando disponibilidad para el vehículo con ID: %s", vehicleId);
        Optional<Vehicle> existingVehicle = vehicleRepository.findByPlate(vehicleId);

        if (existingVehicle.isEmpty()) {
            LOG.warnf("No se encontró el vehículo con Placa: %s. Lanzando excepción VehicleNotFoundByPlateException.", vehicleId);
            throw new VehicleNotFoundByPlateException(vehicleId);
        }

        Vehicle vehicle = existingVehicle.get();
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            LOG.warnf("El vehículo con Placa: %s no está disponible. Estado actual: %s. Lanzando excepción VehicleNotAvailableException.", vehicleId, vehicle.getStatus());
            throw new VehicleNotAvailableException(vehicle.getPlate());
        }

        LOG.debugf("El vehículo con placa: %s está disponible.", vehicleId);
        return vehicle;
    }

    /**
     * Realiza una llamada al proveedor de clima para obtener la información del destino.
     *
     * @param destination El nombre del destino para el cual se quiere consultar el clima.
     * @return Un objeto WeatherInfo con la información del clima.
     */
    private WeatherInfo fetchDestinationWeather(String destination) {
        LOG.debugf("Consultando el proveedor de clima para el destino: %s", destination);
        try {
            return weatherProvider.getCurrentWeather(destination);
        } catch (Exception e) {
            LOG.errorf("Error al consultar el clima para el destino %s: %s", destination, e.getMessage());
            // Retorna un objeto WeatherInfo vacío para evitar un fallo total del sistema.
            return new WeatherInfo();
        }
    }

    /**
     * Mapea el DTO de solicitud a la entidad de dominio Rental.
     *
     * @param request El DTO con los datos de la renta.
     * @return La entidad de dominio Rental con valores por defecto.
     */
    private Rental mapToDomain(RentalRequest request) {
        LOG.debugf("Mapeando el DTO de solicitud a la entidad de dominio Rental.");
        Rental newRental = rentalDtoMapper.toDomain(request);
        newRental.setStatus(RentalStatus.PENDING);
        newRental.setCreatedAt(Instant.now());
        return newRental;
    }

    /**
     * Guarda la entidad de renta en el repositorio.
     *
     * @param rental La entidad de dominio de la renta a guardar.
     * @return La renta guardada con su ID asignado.
     */
    private Rental saveRental(Rental rental) {
        LOG.debugf("Guardando la renta en el repositorio.");
        return rentalRepository.save(rental);
    }

    /**
     * Actualiza el estado del vehículo a 'RENTED' en el repositorio.
     *
     * @param vehicle El vehículo cuya propiedad de estado será actualizada.
     */
    private void updateVehicleStatus(Vehicle vehicle) {
        LOG.debugf("Actualizando el estado del vehículo con ID: %s a RENTED.", vehicle.getId());
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.update(vehicle);
    }
}
