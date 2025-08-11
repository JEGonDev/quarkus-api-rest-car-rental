package org.jegdev.car_rental.vehicles.exceptions.personalized;

import org.jegdev.car_rental.vehicles.exceptions.base.VehicleException;

public class VehicleAlreadyExistsException extends VehicleException {
    public VehicleAlreadyExistsException(String plate) {
        super("VEHICLE-002", String.format("Ya existe un vehículo con la placa: %s", plate));
    }
}
