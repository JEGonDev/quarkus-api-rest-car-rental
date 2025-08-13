package org.jegdev.car_rental.api.weatherApi.client;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.weatherApi.dto.WeatherInfo;
import org.jegdev.car_rental.api.weatherApi.exception.WeatherProviderException;
import org.jegdev.car_rental.api.weatherApi.provider.WeatherProvider;

@Path("/api/weather")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeatherResourceTest {

    @Inject
    WeatherProvider weatherProvider;

    private static final Logger LOG = Logger.getLogger(WeatherResourceTest.class);

    /**
     * Ej: GET /api/weather?q=4.7110,-74.0721
     * q puede ser: "Bogota", "Medellin", "4.7110,-74.0721", etc.
     */
    @GET
    public Response current(
            @Parameter(
                    description = "Ubicación a consultar (ciudad, coordenadas lat,lon, código postal, etc.)",
                    required = true,
                    example = "Bogota"
            )
            @QueryParam("q") String q) {

        LOG.infof("Recibida solicitud de clima para ubicación: %s", q);

        // Validación de parámetros de entrada
        if (q == null || q.isBlank()) {
            LOG.warn("Solicitud rechazada: parámetro 'q' faltante o vacío");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Missing query parameter 'q' (city or lat,lon)\"}")
                    .build();
        }

        try {
            LOG.debugf("Consultando proveedor meteorológico para ubicación: %s", q);
            WeatherInfo info = weatherProvider.getCurrentWeather(q);

            if (info == null) {
                LOG.warnf("No se encontró información meteorológica para: %s", q);
                return Response.status(Response.Status.NO_CONTENT).build();
            }

            LOG.infof("Información meteorológica obtenida exitosamente para: %s (temperatura: %s°C)",
                    info.locationName, info.tempC);
            return Response.ok(info).build();

        } catch (WeatherProviderException e) {
            LOG.errorf(e, "Error del proveedor meteorológico para ubicación '%s': %s", q, e.getMessage());
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity("{\"error\":\"No se pudo obtener clima: " + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {
            LOG.errorf(e, "Error interno inesperado procesando solicitud de clima para ubicación '%s'", q);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error interno\"}")
                    .build();
        }
    }
}
