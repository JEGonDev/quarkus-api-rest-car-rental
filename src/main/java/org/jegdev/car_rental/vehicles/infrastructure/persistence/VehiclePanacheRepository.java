package org.jegdev.car_rental.vehicles.infrastructure.persistence;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.vehicles.infrastructure.entity.VehicleEntity;

@ApplicationScoped
public class VehiclePanacheRepository implements PanacheMongoRepository<VehicleEntity> {
}
