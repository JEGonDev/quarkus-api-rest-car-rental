package org.jegdev.car_rental.api.weatherApi.provider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.client.WeatherApiClient;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherApiResponse;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.exception.WeatherProviderException;

@ApplicationScoped
public class WeatherApiProvider implements WeatherProvider {

    private static final Logger LOG = Logger.getLogger(WeatherApiProvider.class);

    @Inject
    @RestClient
    WeatherApiClient client;

    @ConfigProperty(name = "weatherapi.key")
    String apiKey;

    @Retry(maxRetries = 1) // simple retry
    @CircuitBreaker(requestVolumeThreshold = 5)
    @Override
    public WeatherInfo getCurrentWeather(String locationOrCoords) {
        LOG.infof("Iniciando consulta meteorológica para ubicación: %s", locationOrCoords);

        // Validación de entrada
        if (locationOrCoords == null || locationOrCoords.trim().isEmpty()) {
            LOG.warn("Parámetro de ubicación nulo o vacío");
            throw new WeatherProviderException("La ubicación no puede ser nula o vacía", null);
        }

        try {
            LOG.debugf("Realizando llamada a WeatherAPI con clave: %s y ubicación: %s",
                    maskApiKey(apiKey), locationOrCoords);

            WeatherApiResponse resp = client.getCurrent(apiKey, locationOrCoords, "no");

            if (resp == null || resp.current == null) {
                LOG.errorf("Respuesta vacía de WeatherAPI para ubicación: %s", locationOrCoords);
                throw new RuntimeException("Empty weather response");
            }

            LOG.debugf("Respuesta exitosa recibida de WeatherAPI para ubicación: %s", locationOrCoords);

            WeatherInfo info = new WeatherInfo();
            info.locationName = buildLocationName(resp.location);
            info.tempC = resp.current.tempC;
            info.conditionText = resp.current.condition != null ? resp.current.condition.text : null;
            info.iconUrl = resp.current.condition != null ? resp.current.condition.icon : null;
            info.windKph = resp.current.windKph;
            info.humidity = resp.current.humidity;
            info.lastUpdated = resp.current.lastUpdated;

            LOG.infof("Consulta meteorológica exitosa para %s - Temp: %s°C, Condición: %s",
                    info.locationName, info.tempC, info.conditionText);

            return info;

        } catch (WeatherProviderException e) {
            LOG.errorf("Error de proveedor meteorológico: %s", e.getMessage());
            throw e;

        } catch (jakarta.ws.rs.WebApplicationException e) {
            LOG.errorf(e, "Error HTTP consultando WeatherAPI para ubicación '%s'. Status: %d",
                    locationOrCoords, e.getResponse().getStatus());
            throw new WeatherProviderException("Error HTTP del servicio meteorológico: " + e.getMessage(), e);

        } catch (jakarta.ws.rs.ProcessingException e) {
            LOG.errorf(e, "Error de conectividad consultando WeatherAPI para ubicación '%s'", locationOrCoords);
            throw new WeatherProviderException("Error de conectividad con el servicio meteorológico", e);

        } catch (Exception e) {
            LOG.errorf(e, "Error inesperado consultando WeatherAPI para ubicación '%s': %s",
                    locationOrCoords, e.getMessage());
            throw new WeatherProviderException("No se pudo consultar WeatherAPI: " + e.getMessage(), e);
        }
    }

    private String buildLocationName(WeatherApiResponse.Location loc) {
        LOG.debug("Construyendo nombre de ubicación");

        if (loc == null) {
            LOG.warn("Información de ubicación no disponible");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (loc.name != null) sb.append(loc.name);
        if (loc.region != null && !loc.region.isBlank()) sb.append(", ").append(loc.region);
        if (loc.country != null && !loc.country.isBlank()) sb.append(", ").append(loc.country);

        String locationName = sb.toString();
        LOG.debugf("Nombre de ubicación construido: %s", locationName);

        return locationName;
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}