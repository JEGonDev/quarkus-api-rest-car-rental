package org.jegdev.car_rental.rental.domain.repository;

import org.jegdev.car_rental.rental.domain.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findById(String id);
    Optional<Rental> findByVehicleId(String vehicleId);
    void deleteByOrderId(String id);
    Rental update(Rental rental);
}
