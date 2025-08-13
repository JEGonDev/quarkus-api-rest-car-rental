package org.jegdev.car_rental.rental.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.rental.application.usecase.CreateRentalUseCase;
import org.jegdev.car_rental.rental.application.usecase.GetRentalStatusUseCase;
import org.jegdev.car_rental.rental.application.usecase.UpdateRentalUseCase;
import org.jegdev.car_rental.rental.infrastructure.dto.CreateRentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.UpdateRentalRequest;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

@Path("/rental")
@Tag(name = "Rental", description = "Operaciones relacionadas con las rentas de vehículos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalResource {

    private static final Logger LOG = Logger.getLogger(RentalResource.class.getName());

    private final UpdateRentalUseCase updateRentalUseCase;
    private final CreateRentalUseCase createRentalUseCase;
    private final GetRentalStatusUseCase getRentalStatusUseCase;
    private final RentalDtoMapper rentalDtoMapper;

    @Inject
    public RentalResource(UpdateRentalUseCase updateRentalUseCase, CreateRentalUseCase createRentalUseCase, GetRentalStatusUseCase getRentalStatusUseCase, RentalDtoMapper rentalDtoMapper) {
        this.updateRentalUseCase = updateRentalUseCase;
        this.createRentalUseCase = createRentalUseCase;
        this.getRentalStatusUseCase = getRentalStatusUseCase;
        this.rentalDtoMapper = rentalDtoMapper;
    }

    @POST
    @Operation(summary = "Crear una nueva renta")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Renta creada exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            // Corregido: La respuesta de la API ahora es el DTO CreateRentalResponse
                            schema = @Schema(implementation = CreateRentalResponse.class))),
            @APIResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @APIResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @APIResponse(responseCode = "409", description = "Vehículo no disponible")
    })
    public Response createRental(RentalRequest request) {
        LOG.infof("Recibida petición para crear renta: %s", request);

        // El caso de uso devuelve un CreateRentalResponse que contiene la renta y el clima.
        CreateRentalResponse apiResponse = createRentalUseCase.createRental(request);

        // Corregido: Devolvemos directamente el objeto de respuesta del caso de uso.
        return Response.status(Response.Status.CREATED).entity(apiResponse).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Consultar estado de una renta y el clima de su origen")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Estado de la renta obtenido exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RentalWithWeatherResponse.class))),
            @APIResponse(responseCode = "404", description = "Renta no encontrada")
    })
    public Response getRentalStatus(@PathParam("id") String rentalId) {
        LOG.infof("Recibida petición para consultar el estado de la renta con ID: %s", rentalId);
        RentalWithWeatherResponse response = getRentalStatusUseCase.getRentalStatus(rentalId);
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar una renta")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Renta actualizada exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RentalWithWeatherResponse.class))),
            @APIResponse(responseCode = "404", description = "Renta no encontrada"),
            @APIResponse(responseCode = "400", description = "Datos de actualización inválidos")
    })
    public Response updateRental(@PathParam("id") String rentalId, UpdateRentalRequest request) {
        LOG.infof("Recibida petición para actualizar la renta con ID: %s, datos: %s", rentalId, request);
        RentalWithWeatherResponse response = updateRentalUseCase.updateRental(rentalId, request);
        return Response.ok(response).build();
    }
}
