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
import org.jegdev.car_rental.rental.application.usecase.*;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.rental.infrastructure.dto.CreateRentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.UpdateRentalRequest;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

import java.util.Collections;

@Path("/rental")
@Tag(name = "Rental", description = "Operaciones relacionadas con las rentas de vehículos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalResource {

    private static final Logger LOG = Logger.getLogger(RentalResource.class.getName());

    private final DeleteRentalUseCase deleteRentalUseCase;
    private final GetRentalIdByVehiclePlateUseCase getRentalIdByVehiclePlateUseCase;
    private final UpdateRentalUseCase updateRentalUseCase;
    private final CreateRentalUseCase createRentalUseCase;
    private final GetRentalStatusUseCase getRentalStatusUseCase;

    @Inject
    public RentalResource(DeleteRentalUseCase deleteRentalUseCase,
                          UpdateRentalUseCase updateRentalUseCase,
                          CreateRentalUseCase createRentalUseCase,
                          GetRentalStatusUseCase getRentalStatusUseCase,
                          GetRentalIdByVehiclePlateUseCase getRentalIdByVehiclePlateUseCase)
    {
        this.deleteRentalUseCase = deleteRentalUseCase;
        this.updateRentalUseCase = updateRentalUseCase;
        this.createRentalUseCase = createRentalUseCase;
        this.getRentalStatusUseCase = getRentalStatusUseCase;
        this.getRentalIdByVehiclePlateUseCase = getRentalIdByVehiclePlateUseCase;
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

    @GET
    @Path("/by-plate/{vehiclePlate}")
    @Operation(summary = "Obtener el ID de una renta por la placa del vehículo")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "ID de la renta obtenido exitosamente",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = String.class))),
            @APIResponse(responseCode = "404", description = "Renta no encontrada")
    })
    public Response getRentalIdByVehiclePlate(@PathParam("vehiclePlate") String vehiclePlate) {
        LOG.infof("Recibida solicitud para obtener ID de renta por la placa: %s", vehiclePlate);
        String rentalId = getRentalIdByVehiclePlateUseCase.getRentalIdByVehiclePlate(vehiclePlate);
        return Response.ok(rentalId).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar una renta por su ID")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Renta eliminada exitosamente"),
            @APIResponse(responseCode = "404", description = "Renta no encontrada")
    })
    public Response deleteRental(@PathParam("id") String rentalId) {
        LOG.infof("Recibida solicitud para eliminar la renta con ID: %s", rentalId);
        deleteRentalUseCase.deleteRental(rentalId);
        LOG.infof("Renta con ID: %s eliminada exitosamente.", rentalId);
        return Response.ok().entity(Collections.singletonMap("message", "Renta eliminada exitosamente")).build();
    }
}
