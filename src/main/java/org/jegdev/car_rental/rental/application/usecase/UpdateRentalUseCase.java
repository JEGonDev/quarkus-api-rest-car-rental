package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.provider.WeatherProvider;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.UpdateRentalRequest;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

import java.util.Optional;

/**
 * Caso de uso para actualizar una renta existente.
 * Orquesta la lógica de negocio para buscar una renta, aplicar actualizaciones,
 * consultar el clima del destino si cambió y devolver la respuesta actualizada.
 */
@ApplicationScoped
public class UpdateRentalUseCase {

    private static final Logger LOG = Logger.getLogger(UpdateRentalUseCase.class.getName());

    private final RentalRepository rentalRepository;
    private final RentalDtoMapper rentalDtoMapper;
    private final WeatherProvider weatherProvider;

    @Inject
    public UpdateRentalUseCase(RentalRepository rentalRepository, RentalDtoMapper rentalDtoMapper, WeatherProvider weatherProvider) {
        this.rentalRepository = rentalRepository;
        this.rentalDtoMapper = rentalDtoMapper;
        this.weatherProvider = weatherProvider;
    }

    /**
     * Actualiza una renta existente por su ID y, si el destino cambia, consulta el clima del nuevo destino.
     *
     * @param rentalId El ID de la renta a actualizar.
     * @param request El DTO con los datos actualizados de la renta.
     * @return Un DTO que contiene la renta actualizada y el clima del destino.
     * @throws RentalNotFoundException Si no se encuentra la renta.
     */
    public RentalWithWeatherResponse updateRental(String rentalId, @Valid UpdateRentalRequest request) {
        LOG.infof("Iniciando actualización de la renta con ID: %s", rentalId);

        // Paso 1: Buscar y validar la existencia de la renta
        Rental rental = findRentalById(rentalId);

        // Paso 2: Aplicar las actualizaciones al objeto de dominio
        boolean destinationChanged = applyUpdates(rental, request);

        // Paso 3: Guardar la renta actualizada en el repositorio
        Rental updatedRental = saveRental(rental);

        // Paso 4: Consultar el clima del destino
        WeatherInfo destinationWeather = fetchDestinationWeather(updatedRental);

        // Paso 5: Mapear la entidad a DTO de respuesta
        LOG.debugf("Mapeando renta con ID: %s a DTO de respuesta", rentalId);
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(updatedRental);

        LOG.infof("Renta con ID: %s actualizada exitosamente. Destino: %s", rentalId, updatedRental.getDestination());
        return new RentalWithWeatherResponse(rentalResponse, destinationWeather);
    }

    /**
     * Busca una renta por su ID y lanza una excepción si no se encuentra.
     *
     * @param rentalId El ID de la renta a buscar.
     * @return El objeto de dominio Rental encontrado.
     * @throws RentalNotFoundException Si no se encuentra la renta.
     */
    private Rental findRentalById(String rentalId) {
        LOG.debugf("Buscando renta con ID: %s en el repositorio", rentalId);
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isPresent()) {
            LOG.debugf("Renta con ID: %s encontrada", rentalId);
            return rentalOptional.get();
        } else {
            LOG.warnf("No se encontró renta con ID: %s. Lanzando excepción", rentalId);
            throw new RentalNotFoundException(rentalId);
        }
    }

    /**
     * Aplica las actualizaciones del DTO a la entidad de dominio Rental.
     *
     * @param rental El objeto de dominio Rental a actualizar.
     * @param request El DTO con los datos actualizados.
     * @return true si el destino cambió, false en caso contrario.
     */
    private boolean applyUpdates(Rental rental, UpdateRentalRequest request) {
        LOG.debugf("Aplicando actualizaciones a la renta con ID: %s", rental.getId());
        boolean destinationChanged = false;

        // Actualizar los campos solo si se proporcionan en la solicitud
        Optional.ofNullable(request.getDriverId()).ifPresent(rental::setDriverId);
        Optional.ofNullable(request.getVehicleId()).ifPresent(rental::setVehicleId);
        Optional.ofNullable(request.getStartDate()).ifPresent(rental::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(rental::setEndDate);
        Optional.ofNullable(request.getOrigin()).ifPresent(rental::setOrigin);
        Optional.ofNullable(request.getPrice()).ifPresent(rental::setPrice);

        // Manejar la actualización del destino
        if (request.getDestination() != null && !request.getDestination().equals(rental.getDestination())) {
            LOG.debugf("Destino de la renta con ID: %s cambiado de '%s' a '%s'",
                    rental.getId(), rental.getDestination(), request.getDestination());
            rental.setDestination(request.getDestination());
            destinationChanged = true;
        }

        // Manejar la actualización del estado
        if (request.getStatus() != null && !request.getStatus().equals(rental.getStatus())) {
            LOG.debugf("Estado de la renta con ID: %s cambiado de '%s' a '%s'",
                    rental.getId(), rental.getStatus(), request.getStatus());
            rental.setStatus(request.getStatus());
        }

        return destinationChanged;
    }

    /**
     * Guarda la renta actualizada en el repositorio.
     *
     * @param rental El objeto de dominio Rental a guardar.
     * @return La renta guardada.
     */
    private Rental saveRental(Rental rental) {
        LOG.debugf("Guardando renta actualizada con ID: %s en el repositorio", rental.getId());
        return rentalRepository.update(rental);
    }

    /**
     * Consulta la información del clima para el destino de la renta.
     *
     * @param rental El objeto de dominio Rental con el destino a consultar.
     * @return Un objeto WeatherInfo con los datos del clima.
     */
    private WeatherInfo fetchDestinationWeather(Rental rental) {
        String destination = rental.getDestination();
        LOG.debugf("Consultando información del clima para el destino: %s", destination);
        try {
            WeatherInfo weatherInfo = weatherProvider.getCurrentWeather(destination);
            LOG.debugf("Clima obtenido para el destino: %s, ubicación: %s", destination, weatherInfo.locationName);
            return weatherInfo;
        } catch (Exception e) {
            LOG.errorf("Error al consultar el clima para el destino: %s. Mensaje: %s", destination, e.getMessage());
            return new WeatherInfo();
        }
    }
}