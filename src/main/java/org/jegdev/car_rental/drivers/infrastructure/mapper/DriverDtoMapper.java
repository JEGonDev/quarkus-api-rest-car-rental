package org.jegdev.car_rental.drivers.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverResponse;

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
}