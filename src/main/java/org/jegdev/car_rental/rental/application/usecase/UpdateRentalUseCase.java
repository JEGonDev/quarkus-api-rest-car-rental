package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.provider.WeatherProvider;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.UpdateRentalRequest;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

import java.util.Optional;

/**
 * Caso de uso para actualizar una renta existente.
 * Si se actualiza el destino, también consulta el clima del nuevo destino.
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
     * Actualiza una renta con los datos proporcionados. Si el destino cambia,
     * consulta el clima del nuevo destino.
     *
     * @param rentalId El ID de la renta a actualizar.
     * @param request  El DTO con los datos actualizados.
     * @return La renta actualizada junto con el clima del nuevo destino.
     * @throws RentalNotFoundException si no se encuentra la renta.
     */
    public RentalWithWeatherResponse updateRental(String rentalId, UpdateRentalRequest request) {
        LOG.infof("Iniciando actualización para la renta con ID: %s", rentalId);

        // Paso 1: Buscar la renta por su ID
        Rental rental = findRentalById(rentalId);

        // Paso 2: Aplicar las actualizaciones de forma segura para cada campo
        boolean destinationChanged = applyUpdates(rental, request);

        // Paso 3: Persistir los cambios en la base de datos
        Rental updatedRental = rentalRepository.update(rental);
        LOG.infof("Renta con ID: %s actualizada exitosamente.", rentalId);

        // Paso 4: Obtener el clima del nuevo destino si ha cambiado o del destino actual
        WeatherInfo destinationWeather = fetchDestinationWeather(updatedRental);
        LOG.infof("Clima para el destino '%s' obtenido: %s", updatedRental.getDestination(), destinationWeather.locationName);

        // Paso 5: Mapear la entidad actualizada a un DTO de respuesta
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(updatedRental);

        // Paso 6: Devolver la respuesta encapsulada
        return new RentalWithWeatherResponse(rentalResponse, destinationWeather);
    }

    /**
     * Busca una renta por su ID y lanza una excepción si no la encuentra.
     *
     * @param rentalId El ID de la renta a buscar.
     * @return La renta encontrada.
     * @throws RentalNotFoundException si no se encuentra la renta.
     */
    private Rental findRentalById(String rentalId) {
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);
        if (rentalOptional.isEmpty()) {
            LOG.warnf("Renta no encontrada con ID: %s", rentalId);
            throw new RentalNotFoundException(rentalId);
        }
        return rentalOptional.get();
    }

    /**
     * Aplica las actualizaciones del DTO a la entidad de la renta.
     *
     * @param rental  La entidad de la renta a actualizar.
     * @param request El DTO con los datos actualizados.
     * @return true si el destino fue actualizado, false en caso contrario.
     */
    private boolean applyUpdates(Rental rental, UpdateRentalRequest request) {
        boolean destinationChanged = false;

        // Actualizar los campos solo si se proporcionan en la solicitud
        Optional.ofNullable(request.getDriverId()).ifPresent(rental::setDriverId);
        Optional.ofNullable(request.getVehicleId()).ifPresent(rental::setVehicleId);
        Optional.ofNullable(request.getStartDate()).ifPresent(rental::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(rental::setEndDate);
        Optional.ofNullable(request.getOrigin()).ifPresent(rental::setOrigin);
        Optional.ofNullable(request.getPrice()).ifPresent(rental::setPrice);

        // Manejar la actualización de destino y el cambio de bandera
        if (request.getDestination() != null && !request.getDestination().equals(rental.getDestination())) {
            LOG.debugf("Destino de la renta %s cambiado de '%s' a '%s'", rental.getId(), rental.getDestination(), request.getDestination());
            rental.setDestination(request.getDestination());
            destinationChanged = true;
        }

        // Manejar la actualización del estado
        if (request.getStatus() != null && !request.getStatus().equals(rental.getStatus())) {
            LOG.debugf("Estado de la renta %s cambiado de '%s' a '%s'", rental.getId(), rental.getStatus(), request.getStatus());
            rental.setStatus(request.getStatus());
        }
        return destinationChanged;
    }

    /**
     * Realiza una llamada al proveedor de clima para obtener la información de una ubicación.
     *
     * @param rental La renta de la cual se quiere consultar el clima del destino.
     * @return Un objeto WeatherInfo con la información del clima.
     */
    private WeatherInfo fetchDestinationWeather(Rental rental) {
        String location = rental.getDestination();
        LOG.debugf("Iniciando la llamada al proveedor de clima para el destino: %s", location);
        try {
            return weatherProvider.getCurrentWeather(location);
        } catch (Exception e) {
            LOG.errorf("Error al consultar el clima para la ubicación %s: %s", location, e.getMessage());
            return new WeatherInfo();
        }
    }
}