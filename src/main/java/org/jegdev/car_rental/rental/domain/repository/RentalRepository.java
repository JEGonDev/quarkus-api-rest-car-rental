package org.jegdev.car_rental.rental.domain.repository;

import org.jegdev.car_rental.rental.domain.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findById(String id);
    // Falta implementar obtener id de una renta mediante el vehicleId y eliminar renta
    List<Rental> findByVehicleId(String vehicleId);
    List<Rental> findByDriverId(String driverId);
    Rental update(Rental rental);
}
