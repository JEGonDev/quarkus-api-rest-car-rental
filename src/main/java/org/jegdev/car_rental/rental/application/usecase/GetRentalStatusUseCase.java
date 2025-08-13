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
 * Se encarga de la orquestación: buscar la renta, consultar el clima del origen
 * y construir la respuesta final.
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
     * Consulta el estado de una renta y el clima de su origen.
     *
     * @param rentalId El ID de la renta a buscar.
     * @return Un DTO con la renta y el clima de origen.
     * @throws RentalNotFoundException si no se encuentra la renta.
     */
    public RentalWithWeatherResponse getRentalStatus(String rentalId) {
        LOG.infof("Iniciando consulta para la renta con ID: %s", rentalId);

        // Paso 1: Buscar la renta por su ID en el repositorio
        LOG.debugf("Consultando el repositorio para la renta con ID: %s", rentalId);
        Optional<Rental> rentalOptional = rentalRepository.findById(rentalId);

        if (rentalOptional.isEmpty()) {
            LOG.warnf("Renta no encontrada con ID: %s. Lanzando RentalNotFoundException.", rentalId);
            throw new RentalNotFoundException(rentalId);
        }

        Rental rental = rentalOptional.get();
        LOG.debugf("Renta con ID %s encontrada. Estado actual: %s", rentalId, rental.getStatus());

        // Paso 2: Obtener el clima del origen de la renta
        LOG.debugf("Delegando la consulta del clima a la API externa para el destino: %s", rental.getDestination());
        WeatherInfo originWeather = fetchOriginWeather(rental.getDestination());
        LOG.infof("Clima obtenido para el destino '%s': %s", rental.getDestination(), originWeather.locationName);

        // Paso 3: Mapear la entidad de dominio a un DTO de respuesta
        LOG.debugf("Mapeando la entidad de renta a un DTO de respuesta.");
        RentalResponse rentalResponse = rentalDtoMapper.toResponse(rental);

        // Paso 4: Construir y devolver la respuesta final encapsulada
        LOG.infof("Construyendo la respuesta final para la renta %s.", rentalId);
        return new RentalWithWeatherResponse(rentalResponse, originWeather);
    }

    /**
     * Realiza una llamada al proveedor de clima para obtener la información del origen.
     *
     * @param origin El nombre del origen para el cual se quiere consultar el clima.
     * @return Un objeto WeatherInfo con la información del clima.
     */
    private WeatherInfo fetchOriginWeather(String origin) {
        LOG.debugf("Iniciando la llamada al proveedor de clima para el origen: %s", origin);
        try {
            return weatherProvider.getCurrentWeather(origin);
        } catch (Exception e) {
            LOG.errorf("Error al consultar el clima para el origen %s: %s", origin, e.getMessage());
            // Retorna un objeto WeatherInfo vacío para evitar un fallo total del sistema.
            return new WeatherInfo();
        }
    }
}
