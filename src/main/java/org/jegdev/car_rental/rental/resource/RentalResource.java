package org.jegdev.car_rental.rental.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.infrastructure.dto.CreateRentalResponse;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalDtoMapper;

@Path("/rental")
@Tag(name = "Rental", description = "Operaciones relacionadas con las rentas de vehículos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalController {

    private static final Logger LOG = Logger.getLogger(RentalController.class.getName());

    private final CreateRentalUseCase createRentalUseCase;
    private final RentalDtoMapper rentalDtoMapper;

    @Inject
    public RentalController(CreateRentalUseCase createRentalUseCase, RentalDtoMapper rentalDtoMapper) {
        this.createRentalUseCase = createRentalUseCase;
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

//    @GET
//    @Operation(summary = "Buscar rentas por ID, vehículo o conductor")
//    @APIResponses(value = {
//            @APIResponse(responseCode = "200", description = "Operación exitosa",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RentalResponse.class))),
//            @APIResponse(responseCode = "404", description = "No se encontraron rentas")
//    })
//    public Response getRentals(
//            @Parameter(description = "ID de la renta a buscar")
//            @QueryParam("id") String id,
//            @Parameter(description = "ID del vehículo")
//            @QueryParam("vehiculoId") String vehicleId,
//            @Parameter(description = "ID del conductor")
//            @QueryParam("conductorId") String driverId) {
//        if (id != null) {
//            Rental rental = findRentalByIdUseCase.findRentalById(id);
//            return Response.ok(rentalDtoMapper.toResponse(rental)).build();
//        } else if (vehicleId != null) {
//            List<Rental> rentals = findRentalsByCriteriaUseCase.findByVehicleId(vehicleId);
//            return Response.ok(rentals.stream().map(rentalDtoMapper::toResponse).collect(Collectors.toList())).build();
//        } else if (driverId != null) {
//            List<Rental> rentals = findRentalsByCriteriaUseCase.findByDriverId(driverId);
//            return Response.ok(rentals.stream().map(rentalDtoMapper::toResponse).collect(Collectors.toList())).build();
//        } else {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Debe proporcionar al menos un parámetro de búsqueda (id, vehiculoId o conductorId)").build();
//        }
//    }
//
//    @PUT
//    @Path("/{id}")
//    @Operation(summary = "Actualizar el estado de una renta")
//    @APIResponses(value = {
//            @APIResponse(responseCode = "200", description = "Estado de la renta actualizado",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RentalResponse.class))),
//            @APIResponse(responseCode = "404", description = "Renta no encontrada"),
//            @APIResponse(responseCode = "400", description = "Datos de entrada inválidos")
//    })
//    public Response updateRentalStatus(@PathParam("id") String id, UpdateRentalStatusRequest request) {
//        LOG.infof("Recibida petición para actualizar estado de renta con ID %s a %s", id, request.getStatus());
//        Rental updatedRental = updateRentalStatusUseCase.updateStatus(id, request);
//        return Response.ok(rentalDtoMapper.toResponse(updatedRental)).build();
//    }
//
//    @GET
//    @Path("/{id}/clima")
//    @Operation(summary = "Obtener el pronóstico del clima para el destino de una renta")
//    @APIResponses(value = {
//            @APIResponse(responseCode = "200", description = "Pronóstico obtenido exitosamente",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = WeatherResponse.class))),
//            @APIResponse(responseCode = "404", description = "Renta no encontrada")
//    })
//    public Response getWeatherForecast(@PathParam("id") String id) {
//        LOG.infof("Recibida petición para obtener clima de la renta con ID: %s", id);
//        Rental rental = findRentalByIdUseCase.findRentalById(id);
//        WeatherResponse weather = weatherService.getWeather(rental.getDestination());
//        return Response.ok(weather).build();
//    }
}
