package org.jegdev.car_rental.vehicles.resources;

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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.application.usecase.*;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleRequest;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;

import java.util.Collections;
import java.util.List;

@Path("/vehicles")
@Tag(name = "Vehicles", description = "Operaciones relacionadas con vehículos")
@Produces(MediaType.APPLICATION_JSON) // Define que este recurso produce respuestas en formato JSON
@Consumes(MediaType.APPLICATION_JSON) // Define que este recurso consume solicitudes en formato JSON
public class VehicleResource {

    private static final Logger LOG = Logger.getLogger(VehicleResource.class.getName()); // Logger para registrar información y errores

    private final UpdateVehicleByPlateUseCase updateVehicleByPlateUseCase; // Caso de uso para actualizar un vehículo por su matrícula
    private final DeleteVehicleByPlateUseCase deleteVehicleByPlateUseCase; // Caso de uso para eliminar un vehículo por su matrícula
    private final FindAllVehiclesUseCase findAllVehiclesUseCase; // Caso de uso para obtener todos los vehículos
    private final CreateVehicleUseCase createVehicleUseCase; // Caso de uso para crear un vehículo
    private final GetVehicleByPlateUseCase getVehicleByPlateUseCase; // Caso de uso para obtener un vehículo por su matrícula
    private final VehicleDtoMapper vehicleDtoMapper; // Mapper para convertir entre entidades y DTOs

    @Inject
    public VehicleResource(
            UpdateVehicleByPlateUseCase updateVehicleByPlateUseCase, DeleteVehicleByPlateUseCase deleteVehicleByPlateUseCase,
            FindAllVehiclesUseCase findAllVehiclesUseCase,
            CreateVehicleUseCase createVehicleUseCase,
            GetVehicleByPlateUseCase getVehicleByPlateUseCase,
            VehicleDtoMapper vehicleDtoMapper
    ) {
        this.updateVehicleByPlateUseCase = updateVehicleByPlateUseCase;
        this.deleteVehicleByPlateUseCase = deleteVehicleByPlateUseCase;
        this.findAllVehiclesUseCase = findAllVehiclesUseCase;
        this.createVehicleUseCase = createVehicleUseCase;
        this.getVehicleByPlateUseCase = getVehicleByPlateUseCase;
        this.vehicleDtoMapper = vehicleDtoMapper;
    }

    @DELETE
    @Path("/{plate}")
    @Operation(
        summary = "Eliminar vehículo por matrícula",
        description = "Elimina un vehículo del sistema utilizando su matrícula."
    )
    @APIResponse(
        responseCode = "204",
        description = "Vehículo eliminado exitosamente"
    )
    @APIResponse(
        responseCode = "404",
        description = "Vehículo no encontrado por la matrícula proporcionada"
    )
    public Response deleteVehicleByPlate(
            @Parameter(
                    description = "Matrícula del vehículo a eliminar",
                    example = "ABC123",
                    required = true
            )
            @PathParam("plate") String plate
    ) {
        // Llama al caso de uso para eliminar el vehículo por su matrícula
        LOG.infof("Recibida petición DELETE para eliminar vehículo con matrícula: %s", plate);
        deleteVehicleByPlateUseCase.deleteVehicleByPlate(plate);

        // Devuelve una respuesta HTTP con el código 200 (No Content) indicando que la eliminación fue exitosa
        LOG.infof("Vehículo con matrícula: %s eliminado exitosamente. Devolviendo 200 OK.", plate);
        return Response.ok(Collections.singletonMap("message", "Vehículo eliminado correctamente")).build();
    }

    @POST
    @Operation(
        summary = "Crear un nuevo vehículo",
        description = "Crea un nuevo vehículo en el sistema utilizando los datos proporcionados."
    )
    @RequestBody(
        description = "Datos del vehículo a crear",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = VehicleRequest.class)
        )
    )
    @APIResponse(
        responseCode = "201",
        description = "Vehículo creado exitosamente",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = VehicleResponse.class)
        )
    )
    @APIResponse(
        responseCode = "400",
        description = "Datos de entrada inválidos"
    )
    public Response createVehicle(@Valid VehicleRequest vehicleRequest) {
        LOG.infof("Recibida petición POST para crear un nuevo vehículo con datos: %s", vehicleRequest);
        // Convierte el DTO de solicitud a un objeto de dominio Vehicle
        Vehicle createdVehicle = createVehicleUseCase.createVehicle(vehicleRequest);

        // Convierte el vehículo creado a un DTO de respuesta
        VehicleResponse vehicleResponse = vehicleDtoMapper.toResponse(createdVehicle);
        LOG.infof("Vehículo con matrícula: %s creado exitosamente. Devolviendo 201 Created.", vehicleResponse);
        // Devuelve la respuesta HTTP con el código 201 (Creado) y el vehículo creado
        return Response.status(Response.Status.CREATED)
                .entity(vehicleResponse)
                .build();
    }

    @GET
    @Path("/{plate}")
    @Operation(
        summary = "Obtener vehículo por matrícula",
        description = "Obtiene un vehículo específico utilizando su matrícula."
    )
    @APIResponse(
        responseCode = "200",
        description = "Vehículo encontrado exitosamente",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = VehicleResponse.class)
        )
    )
    @APIResponse(
        responseCode = "404",
        description = "Vehículo no encontrado por la matrícula proporcionada"
    )
    public Response findVehicleByPlate(
            @Parameter(
                    description = "Matrícula del vehículo a buscar",
                    example = "ABC123",
                    required = true
            )
            @PathParam("plate") String plate
    ) {
        LOG.infof("Recibida petición GET para buscar vehículo con matrícula: %s", plate);
        // Llama al caso de uso para obtener el vehículo por su matrícula
        Vehicle vehicle = getVehicleByPlateUseCase.findVehicleByPlate(plate);

        // Convierte el vehículo a un DTO de respuesta
        VehicleResponse vehicleResponse = vehicleDtoMapper.toResponse(vehicle);
        LOG.infof("Vehículo con matrícula: %s encontrado exitosamente. Devolviendo 200 OK.", vehicleResponse);
        // Devuelve la respuesta HTTP con el vehículo encontrado
        return Response.ok(vehicleResponse).build();
    }

    @GET
    @Operation(
            summary = "Obtener todos los vehículos",
            description = "Obtiene una lista de todos los vehículos registrados."
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de vehículos encontrada exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleResponse.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "No se encontraron vehículos"
    )
    public Response findAllVehicles() {
        LOG.info("Recibida petición GET para obtener todos los vehículos.");
        List<VehicleResponse> responseList = findAllVehiclesUseCase.findAllVehicles();

        LOG.infof("Lista de %d vehículos obtenida exitosamente. Devolviendo 200 OK.", responseList);
        return Response.ok(responseList).build();
    }

    @PUT
    @Path("/{plate}")
    @Operation(
            summary = "Actualizar un vehículo",
            description = "Actualiza un vehículo existente utilizando su matrícula como identificador."
    )
    @RequestBody(
            description = "Datos del vehículo a crear",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleRequest.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Vehículo actualizado exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = VehicleResponse.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Vehículo no encontrado"
    )
    @APIResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
    )
    public Response updateVehicleByPlate(
            @Parameter(
                    description = "Matrícula del vehículo a actualizar",
                    example = "ABC123",
                    required = true
            )
            @PathParam("plate") String plate,
            @Valid VehicleRequest vehicleRequest) {
        LOG.infof("Recibida petición PUT para actualizar vehículo con matrícula: %s", plate);
        LOG.infof("Datos del vehículo a actualizar: %s", vehicleRequest);
        Vehicle updatedVehicle = updateVehicleByPlateUseCase.updateVehicleByPlate(plate, vehicleRequest); // Actualiza el vehículo utilizando el caso de uso

        VehicleResponse vehicleResponse = vehicleDtoMapper.toResponse(updatedVehicle); // Convierte el vehículo actualizado a un DTO de respuesta
        LOG.infof("Vehículo con matrícula: %s actualizado exitosamente. Devolviendo 200 OK.", plate);
        LOG.infof("Respuesta del vehículo actualizado: %s", vehicleResponse);
        return Response.ok(vehicleResponse).build(); // Devuelve la respuesta HTTP con el vehículo actualizado
    }
}
