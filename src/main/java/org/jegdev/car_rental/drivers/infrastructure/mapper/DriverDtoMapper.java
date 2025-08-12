package org.jegdev.car_rental.drivers.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverResponse;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase Mapper para convertir entre DTOs y el modelo de dominio Driver.
 * Utiliza el patrón Builder para una construcción de objetos más segura y legible.
 */
@ApplicationScoped
public class DriverDtoMapper {

    /**
     * Convierte un DTO de solicitud DriverRequest a un objeto de dominio Driver.
     * @param request El DTO de solicitud.
     * @return El objeto de dominio Driver.
     */
    public Driver toDomain(DriverRequest request) {
        if (request == null) {
            return null;
        }

        return Driver.builder()
                .name(request.getName())
                .documentId(request.getDocumentId())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
    }

    /**
     * Convierte un DTO de solicitud DriverUpdateRequest a un objeto de dominio Driver.
     * Se usa para actualizaciones, donde algunos campos pueden ser nulos.
     * @param request El DTO de solicitud de actualización.
     * @return El objeto de dominio Driver.
     */
    public Driver toDomain(DriverUpdateRequest request) {
        if (request == null) {
            return null;
        }

        return Driver.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
    }

    /**
     * Convierte un objeto de dominio Driver a un DTO de respuesta DriverResponse.
     * @param driver El objeto de dominio.
     * @return El DTO de respuesta DriverResponse.
     */
    public DriverResponse toResponse(Driver driver) {
        if (driver == null) {
            return null;
        }

        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .documentId(driver.getDocumentId())
                .phoneNumber(driver.getPhoneNumber())
                .email(driver.getEmail())
                .build();
    }

    /**
     * Convierte una lista de Driver (modelo de dominio) a una lista de DriverResponse (DTOs de salida).
     *
     * @param driverList lista de objetos de dominio
     * @return lista de respuestas formateadas para enviar al cliente
     */
    public List<DriverResponse> toResponseList(List<Driver> driverList) {
        return driverList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}