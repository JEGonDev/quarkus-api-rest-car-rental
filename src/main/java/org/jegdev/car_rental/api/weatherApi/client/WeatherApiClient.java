package org.jegdev.car_rental.api.weatherApi.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherApiResponse;

/**
 * Cliente REST para integración con WeatherAPI.com
 *
 * Este cliente permite obtener información meteorológica actual de cualquier ubicación
 * utilizando el servicio externo WeatherAPI. Está configurado como un cliente REST
 * de MicroProfile que será inyectado automáticamente donde sea necesario.
 *
 * La configuración del cliente (URL base, timeouts, etc.) se debe realizar en
 * application.properties bajo la clave "weatherapi".
 *
 */
@RegisterRestClient(configKey = "weatherapi")
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WeatherApiClient {

    /**
     * Obtiene la información meteorológica actual para una ubicación específica.
     *
     * Este método realiza una petición GET al endpoint /current.json de WeatherAPI
     * para obtener datos meteorológicos en tiempo real.
     *
     * @param key La clave API válida de WeatherAPI.com requerida para autenticación
     * @param q La ubicación para consultar. Puede ser:
     *          - Nombre de ciudad: "London" o "Bogotá"
     *          - Coordenadas lat,lon: "48.8567,2.3508"
     *          - Código postal US/UK: "10001" o "SW1"
     *          - Código de aeropuerto: "CDG"
     * @param aqi Indicador para incluir datos de calidad del aire.
     *            Por defecto "no" para mejorar performance
     *
     * @return WeatherApiResponse objeto que contiene toda la información meteorológica
     * @throws jakarta.ws.rs.WebApplicationException si hay errores en la comunicación HTTP
     * @throws jakarta.ws.rs.ProcessingException si hay errores de procesamiento de la respuesta
     */
    @GET
    @Path("/current.json")
    WeatherApiResponse getCurrent(@QueryParam("key") String key,
                                  @QueryParam("q") String q,
                                  @QueryParam("aqi") @DefaultValue("no") String aqi);
}