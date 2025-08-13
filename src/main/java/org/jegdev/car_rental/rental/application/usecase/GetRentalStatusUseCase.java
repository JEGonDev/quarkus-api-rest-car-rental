package org.jegdev.car_rental.rental.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.provider.WeatherProvider;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

import java.util.Optional;

/**
 * Caso de uso para consultar el estado de una renta por su ID.
 * Orquesta la lógica de negocio para buscar una renta, consultar el clima del destino
 * y construir una respuesta que incluye ambos datos.
 */
@ApplicationScoped
public class GetRentalStatusUseCase {

    private static final Logger LOG = Logger.getLogger(GetRentalStatusUseCase.class.getName());

    private final RentalRepository rentalRepository;
    private final RentalDtoMapper rentalDtoMapper;
    private final WeatherProvider weatherProvider;

    @Inject
    public GetRentalStatusUseCase(RentalRepository rentalRepository, RentalDtoMapper rentalDtoMapper, WeatherProvider weatherProvider) {
        this.rentalRepository = rentalRepository;
        this.rentalDtoMapper = rentalDtoMapper;
        this.weatherProvider = weatherProvider;
    }

    /**
     * Consulta el estado de una renta por su ID y el clima de su destino.
     *
     * @param rentalId El ID de la renta a consultar.
     * @return Un DTO que contiene la información de la renta y el clima del destino.
     * @throws RentalNotFoundException Si no se encuentra la renta.
     */
    public RentalWithWeatherResponse getRentalStatus(String rentalId) {
        LOG.infof("Iniciando consulta de estado para la renta con ID: %s", rentalId);

        // Paso 1: Buscar y validar la existencia de la renta
        Rental rental = findRentalById(rentalId);

        // Paso 2: Consultar el clima del destino
        WeatherInfo destinationWeather = fetchDestinationWeather(rental.getDestination());

        // Paso 3: Mapear la entidad a DTO de respuesta
        LOG.debugf("Mapeando renta con ID: %s a DTO de respuesta", rentalId);
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(rental);

        LOG.infof("Consulta de estado para la renta con ID: %s completada exitosamente", rentalId);
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
            LOG.debugf("Renta con ID: %s encontrada, estado: %s", rentalId, rentalOptional.get().getStatus());
            return rentalOptional.get();
        } else {
            LOG.warnf("No se encontró renta con ID: %s. Lanzando excepción", rentalId);
            throw new RentalNotFoundException(rentalId);
        }
    }

    /**
     * Consulta la información del clima para el destino de la renta.
     *
     * @param destination El destino para el cual se consulta el clima.
     * @return Un objeto WeatherInfo con los datos del clima.
     */
    private WeatherInfo fetchDestinationWeather(String destination) {
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