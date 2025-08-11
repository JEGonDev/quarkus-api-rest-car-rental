package org.jegdev.car_rental.shared.errors;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jegdev.car_rental.shared.Response.ErrorResponse;

import java.time.Instant;
import java.util.Collections;

/**
 * Manejador global de excepciones para la aplicación.
 * La anotación @Provider indica a JAX-RS que esta clase provee funcionalidad para el framework.
 * ExceptionMapper<Exception> indica que esta clase manejará todas las excepciones que hereden de Exception.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    /**
     * @Context inyecta información sobre la URI de la solicitud actual.
     * UriInfo permite obtener detalles sobre la ruta que generó el error.
     */
    @Context
    UriInfo uriInfo;

    /**
     * Método principal que convierte una excepción en una respuesta HTTP.
     * Se ejecuta automáticamente cuando ocurre cualquier excepción no manejada en la aplicación.
     *
     * @param exception La excepción capturada
     * @return Response Respuesta HTTP formateada con los detalles del error
     */
    @Override
    public Response toResponse(Exception exception) {
//      Determina si es una excepción personalizada (ApiException) o no
//      Si la excepción capturada es del tipo ApiException, maneja esa excepción con un método especializado
        if (exception instanceof ApiException) {
            return handleApiException((ApiException) exception);
        }
        return handleUnexpectedException(exception);
    }

    /**
     * Maneja las excepciones personalizadas de tipo ApiException.
     *
     * @param apiException La excepción personalizada
     * @return Response Respuesta HTTP con código 400 (Bad Request)
     */
    private Response handleApiException(ApiException apiException) {
        // Se construye la respuesta de error con los detalles de la excepcion
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(apiException.getCode())        // Código personalizado del error
                .message(apiException.getMessage())   // Mensaje descriptivo
                .timestamp(Instant.now())   // Momento en que ocurrió
                .path(uriInfo.getPath())   // Ruta que generó el error
                // Se agrega el mensaje de error dentro de una lista para mantener el campo 'details' como una colección,
                // lo cual permite agregar múltiples detalles si es necesario en el futuro.
                .details(Collections.singletonList(apiException.getMessage()))
                .build();

        // Se registra el error en los logs
        Log.errorf("Error procesando la solicitud: {}", errorResponse);

        // Construye y retorna la respuesta HTTP
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse) // El cuerpo de la respuesta
                .build();
    }

    /**
     * Maneja las excepciones no esperadas o no controladas.
     *
     * @param exception La excepción no controlada
     * @return Response Respuesta HTTP con código 500 (Internal Server Error)
     */
    private Response handleUnexpectedException(Exception exception) {
        // Similar al anterior pero para errores internos/no esperados
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_ERROR")
                .message("Ha ocurrido un error interno")
                .timestamp(Instant.now())
                .path(uriInfo.getPath())
                .details(Collections.singletonList(exception.getMessage()))
                .build();

        // Se registra el error en los logs
        Log.errorf("Error no controlado: ", errorResponse);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse) // El cuerpo de la respuesta
                .build();
    }
}
