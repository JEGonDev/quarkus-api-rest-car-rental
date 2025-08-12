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
import org.jegdev.car_rental.drivers.application.usecase.CreateDriverUseCase;
import org.jegdev.car_rental.drivers.application.usecase.FindDriverByDocumentIdUseCase;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverResponse;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverDtoMapper;

/**
 * Recurso REST para la gestión de conductores.
 * Proporciona endpoints para crear y consultar información de conductores.
 */
@Path("/drivers")
@Tag(name = "Drivers", description = "Operaciones relacionadas con los conductores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DriverResource {

    private final CreateDriverUseCase createDriverUseCase;
    private final FindDriverByDocumentIdUseCase findDriverByDocumentIdUseCase;
    private final DriverDtoMapper driverDtoMapper;

    @Inject
    public DriverResource(CreateDriverUseCase createDriverUseCase,
                          FindDriverByDocumentIdUseCase findDriverByDocumentIdUseCase,
                          DriverDtoMapper driverDtoMapper) {
        this.createDriverUseCase = createDriverUseCase;
        this.findDriverByDocumentIdUseCase = findDriverByDocumentIdUseCase;
        this.driverDtoMapper = driverDtoMapper;
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
