package org.jegdev.car_rental.rental.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalRequest;
import org.jegdev.car_rental.rental.infrastructure.dto.RentalResponse;

import java.util.Objects;

@ApplicationScoped
public class RentalDtoMapper {

    // Convierte un DTO de solicitud (RentalRequest) a una entidad de dominio (Rental).
    public Rental toDomain(RentalRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }

        return Rental.builder()
                .vehicleId(request.getVehicleId())
                .driverId(request.getDriverId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .price(request.getPrice())
                .build();
    }

    // Convierte una entidad de dominio (Rental) a un DTO de respuesta (RentalResponse).
    public RentalResponse toResponse(Rental rental) {
        if (Objects.isNull(rental)) {
            return null;
        }

        return RentalResponse.builder()
                .id(rental.getId())
                .vehicleId(rental.getVehicleId())
                .driverId(rental.getDriverId())
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .origin(rental.getOrigin())
                .destination(rental.getDestination())
                .price(rental.getPrice())
                .status(rental.getStatus())
                .createdAt(rental.getCreatedAt())
                .build();
    }
}
