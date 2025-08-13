package org.jegdev.car_rental.rental.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.jegdev.car_rental.rental.infrastructure.entity.RentalEntity;

public interface RentalPanacheRepository extends PanacheMongoRepository<RentalEntity> {
}
