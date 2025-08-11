package org.jegdev.car_rental.vehicles.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jegdev.car_rental.vehicles.application.usecase.GetVehicleByPlateUseCase;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;

@Path("/vehicles")
@Tag(name = "Vehicles", description = "Operaciones relacionadas con vehículos")
@Produces(MediaType.APPLICATION_JSON)
public class VehicleResource {

    private final GetVehicleByPlateUseCase getVehicleByPlateUseCase; // Caso de uso para obtener un vehículo por su matrícula
    private final VehicleDtoMapper vehicleDtoMapper; // Mapper para convertir entre entidades y DTOs

    @Inject
    public VehicleResource(GetVehicleByPlateUseCase getVehicleByPlateUseCase, VehicleDtoMapper vehicleDtoMapper) {
        this.getVehicleByPlateUseCase = getVehicleByPlateUseCase;
        this.vehicleDtoMapper = vehicleDtoMapper;
    }

    @GET
    @Path("/{plate}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Obtener vehículo por matrícula",
        description = "Obtiene un vehículo específico utilizando su matrícula."
    )
    @APIResponse(
        responseCode = "200",
        description = "Vehículo encontrado exitosamente",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @org.eclipse.microprofile.openapi.annotations.media.Schema(implementation = VehicleResponse.class)
        )
    )
    @APIResponse(
        responseCode = "404",
        description = "Vehículo no encontrado por la matrícula proporcionada"
    )
    public Response findVehicleByPlate(@PathParam("plate") String plate) {
        // Llama al caso de uso para obtener el vehículo por su matrícula
        Vehicle vehicle = getVehicleByPlateUseCase.findVehicleByPlate(plate);

        // Convierte el vehículo a un DTO de respuesta
        VehicleResponse vehicleResponse = vehicleDtoMapper.toResponse(vehicle);

        // Devuelve la respuesta HTTP con el vehículo encontrado
        return Response.ok(vehicleResponse).build();
    }
}
