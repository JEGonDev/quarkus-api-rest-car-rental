package org.jegdev.car_rental.rental.infrastructure.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

import java.time.Instant;

// Esta clase representa la entidad de persistencia para una renta en MongoDB,
// utilizando Panache para la gestión de datos.
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@MongoEntity(collection = "rentals")
public class RentalEntity extends PanacheMongoEntity {
    public String vehicleId;
    public String driverId;
    public Instant startDate;
    public Instant endDate;
    public String origin;
    public String destination;
    public double price;
    public RentalStatus status;
    public Instant createdAt;
}
