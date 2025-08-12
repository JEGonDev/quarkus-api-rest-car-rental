package org.jegdev.car_rental.drivers.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.drivers.infrastructure.entity.DriverEntity;

@ApplicationScoped
public class DriverPanacheRepository implements PanacheMongoRepository<DriverEntity> {
}
