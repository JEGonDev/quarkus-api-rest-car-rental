package org.jegdev.car_rental.rental.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.infrastructure.entity.RentalEntity;

import java.util.Objects;

@ApplicationScoped
public class RentalPersistenceMapper {

    // Convierte un Rental (dominio) a un RentalEntity (persistencia).
    public RentalEntity toEntity(Rental rental) {
        if (Objects.isNull(rental)) {
            return null;
        }

        RentalEntity entity = new RentalEntity();
        if (Objects.nonNull(rental.getId())) {
            entity.id = new ObjectId(rental.getId());
        }
        entity.vehicleId = rental.getVehicleId();
        entity.driverId = rental.getDriverId();
        entity.startDate = rental.getStartDate();
        entity.endDate = rental.getEndDate();
        entity.origin = rental.getOrigin();
        entity.destination = rental.getDestination();
        entity.price = rental.getPrice();
        entity.status = rental.getStatus();
        entity.createdAt = rental.getCreatedAt();
        return entity;
    }

    // Convierte un RentalEntity (persistencia) a un Rental (dominio).
    public Rental toDomain(RentalEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return Rental.builder()
                .id(entity.id.toHexString())
                .vehicleId(entity.vehicleId)
                .driverId(entity.driverId)
                .startDate(entity.startDate)
                .endDate(entity.endDate)
                .origin(entity.origin)
                .destination(entity.destination)
                .price(entity.price)
                .status(entity.status)
                .createdAt(entity.createdAt)
                .build();
    }
}
