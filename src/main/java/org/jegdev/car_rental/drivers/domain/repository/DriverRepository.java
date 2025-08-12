package org.jegdev.car_rental.drivers.domain.repository;

import org.jegdev.car_rental.drivers.domain.model.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {
    Driver save(Driver driver);
    Optional<Driver> findByDocumentId(String documentId);
    List<Driver> findAll();
    Driver update(Driver driver);
    void deleteByDocumentId(String documentId);
}
