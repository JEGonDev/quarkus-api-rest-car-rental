package org.jegdev.car_rental.rental.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.rental.application.usecase.*;
import org.jegdev.car_rental.rental.infrastructure.dto.CreateRentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalWithWeatherResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.UpdateRentalRequest;

import java.util.Collections;

/**
 * Recurso REST para la gestión de rentas de vehículos.
 * Proporciona endpoints para crear, consultar, actualizar y eliminar rentas, incluyendo información del clima.
 */
@Path("/rental")
@Tag(name = "Rental", description = "Operaciones relacionadas con la gestión de rentas de vehículos")
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
                          GetRentalIdByVehiclePlateUseCase getRentalIdByVehiclePlateUseCase) {
        this.deleteRentalUseCase = deleteRentalUseCase;
        this.updateRentalUseCase = updateRentalUseCase;
        this.createRentalUseCase = createRentalUseCase;
        this.getRentalStatusUseCase = getRentalStatusUseCase;
        this.getRentalIdByVehiclePlateUseCase = getRentalIdByVehiclePlateUseCase;
    }

    /**
     * Endpoint para crear una nueva renta de vehículo.
     *
     * @param request DTO con los datos de la renta a crear.
     * @return La renta creada junto con el clima del destino, con código de estado 201 (Created).
     */
    @POST
    @Operation(
            summary = "Crear una nueva renta",
            description = "Crea una nueva renta en el sistema con los datos proporcionados, incluyendo información del clima del destino."
    )
    @RequestBody(
            description = "Datos de la renta a crear",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RentalRequest.class)
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Renta creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CreateRentalResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Vehículo no encontrado"
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Vehículo no disponible"
            )
    })
    public Response createRental(@Valid RentalRequest request) {
        LOG.infof("Recibida solicitud para crear una nueva renta con datos: %s", request);
        CreateRentalResponse apiResponse = createRentalUseCase.createRental(request);
        LOG.infof("Renta creada exitosamente con datos: %s", apiResponse);
        return Response.status(Response.Status.CREATED).entity(apiResponse).build();
    }

    /**
     * Endpoint para consultar el estado de una renta y el clima de su destino.
     *
     * @param rentalId El ID de la renta a consultar.
     * @return La renta con su estado y el clima del destino, con código de estado 200 (OK).
     */
    @GET
    @Path("/{id}")
    @Operation(
            summary = "Consultar estado de una renta",
            description = "Obtiene el estado de una renta específica por su ID, junto con la información del clima del destino."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Estado de la renta obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RentalWithWeatherResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Renta no encontrada"
            )
    })
    public Response getRentalStatus(@Parameter(
            name = "id",
            description = "El ID de la renta a consultar",
            required = true,
            example = "60f7b1a2c3e4b123456789ab"
    ) @PathParam("id") String rentalId) {
        LOG.infof("Recibida solicitud para consultar el estado de la renta con ID: %s", rentalId);
        RentalWithWeatherResponse response = getRentalStatusUseCase.getRentalStatus(rentalId);
        LOG.infof("Estado de la renta con ID: %s obtenido exitosamente: %s", rentalId, response);
        return Response.ok(response).build();
    }

    /**
     * Endpoint para actualizar una renta existente.
     *
     * @param rentalId El ID de la renta a actualizar.
     * @param request DTO con los datos actualizados de la renta.
     * @return La renta actualizada con el clima del destino, con código de estado 200 (OK).
     */
    @PUT
    @Path("/{id}")
    @Operation(
            summary = "Actualizar una renta",
            description = "Actualiza una renta existente utilizando su ID, incluyendo información del clima del destino si cambió."
    )
    @RequestBody(
            description = "Datos actualizados de la renta",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UpdateRentalRequest.class)
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Renta actualizada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = RentalWithWeatherResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Renta no encontrada"
            )
    })
    public Response updateRental(@Parameter(
            name = "id",
            description = "El ID de la renta a actualizar",
            required = true,
            example = "60f7b1a2c3e4b123456789ab"
    ) @PathParam("id") String rentalId, @Valid UpdateRentalRequest request) {
        LOG.infof("Recibida solicitud para actualizar la renta con ID: %s, datos: %s", rentalId, request);
        RentalWithWeatherResponse response = updateRentalUseCase.updateRental(rentalId, request);
        LOG.infof("Renta con ID: %s actualizada exitosamente: %s", rentalId, response);
        return Response.ok(response).build();
    }

    /**
     * Endpoint para obtener el ID de una renta por la placa del vehículo.
     *
     * @param vehiclePlate La placa del vehículo asociado a la renta.
     * @return El ID de la renta, con código de estado 200 (OK).
     */
    @GET
    @Path("/by-plate/{vehiclePlate}")
    @Operation(
            summary = "Obtener ID de renta por placa de vehículo",
            description = "Busca y devuelve el ID de una renta activa asociada a la placa del vehículo proporcionada."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "ID de la renta obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN,
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Renta no encontrada para la placa proporcionada"
            )
    })
    public Response getRentalIdByVehiclePlate(@Parameter(
            name = "vehiclePlate",
            description = "La placa del vehículo asociado a la renta",
            required = true,
            example = "ABC123"
    ) @PathParam("vehiclePlate") String vehiclePlate) {
        LOG.infof("Recibida solicitud para obtener el ID de renta por la placa de vehículo: %s", vehiclePlate);
        String rentalId = getRentalIdByVehiclePlateUseCase.getRentalIdByVehiclePlate(vehiclePlate);
        LOG.infof("ID de renta obtenido para la placa de vehículo: %s, ID: %s", vehiclePlate, rentalId);
        return Response.ok(rentalId).build();
    }

    /**
     * Endpoint para eliminar una renta por su ID.
     *
     * @param rentalId El ID de la renta a eliminar.
     * @return Un código de estado 200 (OK) con un mensaje de confirmación.
     */
    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Eliminar una renta",
            description = "Elimina una renta existente por su ID y marca el vehículo asociado como disponible."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Renta eliminada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(example = "{\"message\": \"Renta eliminada exitosamente\"}")
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Renta no encontrada"
            )
    })
    public Response deleteRental(@Parameter(
            name = "id",
            description = "El ID de la renta a eliminar",
            required = true,
            example = "60f7b1a2c3e4b123456789ab"
    ) @PathParam("id") String rentalId) {
        LOG.infof("Recibida solicitud para eliminar la renta con ID: %s", rentalId);
        deleteRentalUseCase.deleteRental(rentalId);
        LOG.infof("Renta con ID: %s eliminada exitosamente", rentalId);
        return Response.ok(Collections.singletonMap("message", "Renta eliminada exitosamente")).build();
    }
}