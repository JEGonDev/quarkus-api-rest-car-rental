package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

/**
 * Caso de uso para la creación de una nueva renta de vehículo.
 * Orquesta la lógica de negocio para validar la disponibilidad del vehículo,
 * consultar el clima del destino, mapear los datos y guardar la renta.
 */
@ApplicationScoped
public class CreateRentalUseCase {

    private static final Logger LOG = Logger.getLogger(CreateRentalUseCase.class.getName());

    private final RentalRepository rentalRepository;
    private final RentalDtoMapper rentalDtoMapper;
    private final VehicleRepository vehicleRepository;
    private final WeatherProvider weatherProvider;

    @Inject
    public CreateRentalUseCase(RentalRepository rentalRepository, RentalDtoMapper rentalDtoMapper,
                               VehicleRepository vehicleRepository, WeatherProvider weatherProvider) {
        this.rentalRepository = rentalRepository;
        this.rentalDtoMapper = rentalDtoMapper;
        this.vehicleRepository = vehicleRepository;
        this.weatherProvider = weatherProvider;
    }

    /**
     * Crea una nueva renta para un vehículo, incluyendo la consulta del clima del destino.
     *
     * @param request El DTO con los datos de la solicitud de renta.
     * @return Un DTO que contiene la información de la renta creada y el clima del destino.
     * @throws VehicleNotFoundByPlateException Si el vehículo no existe.
     * @throws VehicleNotAvailableException Si el vehículo no está disponible.
     */
    public CreateRentalResponse createRental(@Valid RentalRequest request) {
        LOG.infof("Iniciando creación de renta para el vehículo con placa: %s", request.getVehicleId());

        // Paso 1: Validar que el vehículo exista y esté disponible
        Vehicle vehicle = validateVehicleAvailability(request.getVehicleId());

        // Paso 2: Consultar el clima del destino
        WeatherInfo destinationWeather = fetchDestinationWeather(request.getDestination());

        // Paso 3: Mapear el DTO a la entidad de dominio
        Rental newRental = mapToDomain(request);

        // Paso 4: Guardar la renta en la base de datos
        Rental savedRental = saveRental(newRental);

        // Paso 5: Actualizar el estado del vehículo a RENTED
        updateVehicleStatus(vehicle);

        // Paso 6: Mapear la entidad a DTO de respuesta
        LOG.debugf("Mapeando renta con ID: %s a DTO de respuesta", savedRental.getId());
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(savedRental);

        LOG.infof("Renta con ID: %s creada exitosamente para el vehículo con placa: %s",
                savedRental.getId(), vehicle.getPlate());
        return new CreateRentalResponse(rentalResponse, destinationWeather);
    }

    /**
     * Valida la existencia y disponibilidad del vehículo por su placa.
     *
     * @param vehicleId La placa del vehículo a validar.
     * @return El objeto de dominio del vehículo si está disponible.
     * @throws VehicleNotFoundByPlateException Si el vehículo no se encuentra.
     * @throws VehicleNotAvailableException Si el vehículo no está disponible.
     */
    private Vehicle validateVehicleAvailability(String vehicleId) {
        LOG.debugf("Buscando vehículo con placa: %s para verificar su existencia y disponibilidad", vehicleId);
        Optional<Vehicle> existingVehicle = vehicleRepository.findByPlate(vehicleId);

        if (existingVehicle.isEmpty()) {
            LOG.warnf("No se encontró vehículo con placa: %s. Lanzando excepción", vehicleId);
            throw new VehicleNotFoundByPlateException(vehicleId);
        }

        Vehicle vehicle = existingVehicle.get();
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            LOG.warnf("Vehículo con placa: %s no está disponible. Estado actual: %s",
                    vehicleId, vehicle.getStatus());
            throw new VehicleNotAvailableException(vehicle.getPlate());
        }

        LOG.debugf("Vehículo con placa: %s verificado como disponible", vehicleId);
        return vehicle;
    }

    /**
     * Consulta la información del clima para el destino especificado.
     *
     * @param destination El destino para el cual se consulta el clima.
     * @return Un objeto WeatherInfo con los datos del clima.
     */
    private WeatherInfo fetchDestinationWeather(String destination) {
        LOG.debugf("Consultando información del clima para el destino: %s", destination);
        try {
            WeatherInfo weatherInfo = weatherProvider.getCurrentWeather(destination);
            LOG.debugf("Clima obtenido para el destino: %s, ubicación: %s",
                    destination, weatherInfo.locationName);
            return weatherInfo;
        } catch (Exception e) {
            LOG.errorf("Error al consultar el clima para el destino: %s. Mensaje: %s",
                    destination, e.getMessage());
            return new WeatherInfo();
        }
    }

    /**
     * Mapea el DTO de solicitud a la entidad de dominio Rental.
     *
     * @param request El DTO con los datos de la renta.
     * @return La entidad de dominio Rental inicializada.
     */
    private Rental mapToDomain(RentalRequest request) {
        LOG.debugf("Mapeando DTO de solicitud a entidad de dominio para la renta");
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
        LOG.debugf("Guardando renta en el repositorio");
        return rentalRepository.save(rental);
    }

    /**
     * Actualiza el estado del vehículo a RENTED en el repositorio.
     *
     * @param vehicle El objeto de dominio del vehículo a actualizar.
     */
    private void updateVehicleStatus(Vehicle vehicle) {
        LOG.debugf("Actualizando estado del vehículo con placa: %s a RENTED", vehicle.getPlate());
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.update(vehicle);
    }
}