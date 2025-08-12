package org.jegdev.car_rental.drivers.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.jegdev.car_rental.drivers.infrastructure.entity.DriverEntity;

public class DriverPanacheRepository implements PanacheMongoRepository<DriverEntity> {
}
