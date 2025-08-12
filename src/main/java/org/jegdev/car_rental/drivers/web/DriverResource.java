package org.jegdev.car_rental.drivers.web;

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
import org.jegdev.car_rental.drivers.application.usecase.*;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverResponse;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverUpdateRequest;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverDtoMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recurso REST para la gestión de conductores.
 * Proporciona endpoints para crear y consultar información de conductores.
 */
@Path("/drivers")
@Tag(name = "Drivers", description = "Operaciones relacionadas con los conductores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DriverResource {

    private final DeleteDriverUseCase deleteDriverUseCase;
    private final FindAllDriversUseCase findAllDriversUseCase;
    private final UpdateDriverUseCase updateDriverUseCase;
    private final CreateDriverUseCase createDriverUseCase;
    private final FindDriverByDocumentIdUseCase findDriverByDocumentIdUseCase;
    private final DriverDtoMapper driverDtoMapper;

    @Inject
    public DriverResource(DeleteDriverUseCase deleteDriverUseCase, FindAllDriversUseCase findAllDriversUseCase,
                          UpdateDriverUseCase updateDriverUseCase,
                          CreateDriverUseCase createDriverUseCase,
                          FindDriverByDocumentIdUseCase findDriverByDocumentIdUseCase,
                          DriverDtoMapper driverDtoMapper) {
        this.deleteDriverUseCase = deleteDriverUseCase;
        this.findAllDriversUseCase = findAllDriversUseCase;
        this.updateDriverUseCase = updateDriverUseCase;
        this.createDriverUseCase = createDriverUseCase;
        this.findDriverByDocumentIdUseCase = findDriverByDocumentIdUseCase;
        this.driverDtoMapper = driverDtoMapper;
    }

    /**
     * Endpoint para eliminar un conductor por su ID de documento.
     * @param documentId El ID del documento del conductor a eliminar.
     * @return Un código de estado 204 (No Content) si la eliminación fue exitosa.
     */
    @DELETE
    @Path("/{documentId}")
    @Operation(
            summary = "Eliminar un conductor",
            description = "Elimina un conductor existente por su ID de documento."
    )
    @APIResponse(
            responseCode = "204",
            description = "Conductor eliminado exitosamente"
    )
    @APIResponse(
            responseCode = "404",
            description = "Conductor no encontrado por el ID de documento proporcionado"
    )
    public Response deleteDriver(@Parameter(
            name = "documentId",
            description = "El ID del documento del conductor a eliminar",
            required = true,
            example = "123456789"
    ) @PathParam("documentId") String documentId) {
        deleteDriverUseCase.deleteDriverByDocumentId(documentId);
        return Response.ok(Collections.singletonMap("message", "Usuario eliminado correctamente")).build();
    }

    /**
     * Endpoint para obtener todos los conductores.
     * @return Una lista de todos los conductores.
     */
    @GET
    @Operation(
            summary = "Obtener todos los conductores",
            description = "Obtiene una lista de todos los conductores registrados en el sistema."
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista de conductores obtenida exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverResponse.class)
            )
    )
    public Response findAllDrivers() {
        // Llamar al caso de uso para obtener todos los conductores
        List<Driver> driverList = findAllDriversUseCase.findAllDrivers();
        // Mapear la lista de entidades de dominio a una lista de DTOs de respuesta
        List<DriverResponse> driverResponses = driverDtoMapper.toResponseList(driverList);
        // Devolver la respuesta con el código 200 (OK) y la lista de conductores
        return Response.ok(driverResponses).build();
    }

    /**
     * Endpoint para actualizar un conductor existente por su ID de documento.
     * @param documentId El ID del documento del conductor a actualizar.
     * @param driverRequest DTO con los nuevos datos del conductor.
     * @return El conductor actualizado con el código de estado 200 (OK).
     */
    @PUT
    @Path("/{documentId}")
    @Operation(
            summary = "Actualizar un conductor",
            description = "Actualiza un conductor existente utilizando su ID de documento como identificador."
    )
    @RequestBody(
            description = "Datos del conductor a actualizar",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverUpdateRequest.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Conductor actualizado exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverResponse.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Conductor no encontrado"
    )
    @APIResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
    )
    public Response updateDriverByDocumentId(@Parameter(
                                                     name = "documentId",
                                                     description = "El ID del documento del conductor a buscar",
                                                     required = true,
                                                     example = "123456789"
                                             ) @PathParam("documentId") String documentId,
                                             @Valid DriverUpdateRequest driverRequest) {
        Driver updatedDriver = updateDriverUseCase.updateDriverByDocumentId(documentId, driverRequest);
        DriverResponse driverResponse = driverDtoMapper.toResponse(updatedDriver);
        return Response.ok(driverResponse).build();
    }

    /**
     * Endpoint para crear un nuevo conductor.
     * @param driverRequest DTO con los datos del conductor a crear.
     * @return El conductor creado con el código de estado 201 (Created).
     */
    @POST
    @Operation(
            summary = "Crear un nuevo conductor",
            description = "Crea un nuevo conductor en el sistema con los datos proporcionados."
    )
    @RequestBody(
            description = "Datos del conductor a crear",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverRequest.class)
            )
    )
    @APIResponse(
            responseCode = "201",
            description = "Conductor creado exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverResponse.class)
            )
    )
    @APIResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos"
    )
    public Response createDriver(@Valid DriverRequest driverRequest) {
        // Llamar al caso de uso para crear el conductor
        Driver createdDriver = createDriverUseCase.createDriver(driverRequest);
        // Mapear la entidad de dominio a un DTO de respuesta
        DriverResponse driverResponse = driverDtoMapper.toResponse(createdDriver);
        // Devolver la respuesta con el código 201 (Created)
        return Response.status(Response.Status.CREATED)
                .entity(driverResponse)
                .build();
    }

    /**
     * Endpoint para buscar un conductor por su ID de documento.
     * @param documentId El ID del documento del conductor.
     * @return El conductor encontrado.
     */
    @GET
    @Path("/{documentId}")
    @Operation(
            summary = "Obtener un conductor por su ID de documento",
            description = "Busca y devuelve un conductor específico utilizando su ID de documento."
    )
    @APIResponse(
            responseCode = "200",
            description = "Conductor encontrado exitosamente",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DriverResponse.class)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Conductor no encontrado por el ID de documento proporcionado"
    )
    public Response findDriverByDocumentId(
            @Parameter(
                    description = "ID del documento del conductor a buscar",
                    required = true,
                    example = "123456789"
            )
            @PathParam("documentId") String documentId
    ) {
        // Llamar al caso de uso para buscar el conductor por su ID de documento
        Driver driver = findDriverByDocumentIdUseCase.findDriverByDocumentId(documentId);
        // Mapear la entidad de dominio a un DTO de respuesta
        DriverResponse driverResponse = driverDtoMapper.toResponse(driver);
        // Devolver la respuesta con el código 200 (OK)
        return Response.ok(driverResponse).build();
    }
}
