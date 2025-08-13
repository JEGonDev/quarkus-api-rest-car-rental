package org.jegdev.car_rental.rental.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.rental.infrastructure.entity.RentalEntity;

@ApplicationScoped
public class RentalPanacheRepository implements PanacheMongoRepository<RentalEntity> {
}
