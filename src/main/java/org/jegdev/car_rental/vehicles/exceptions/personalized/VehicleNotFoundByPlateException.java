package org.jegdev.car_rental.vehicles.exceptions.personalized;

import org.jegdev.car_rental.vehicles.exceptions.base.VehicleException;

public class VehicleNotFoundByPlateException extends VehicleException {
    public VehicleNotFoundByPlateException(String plate) {
        super("VEHICLE-001", String.format("No se encontró el vehículo con la placa: %s", plate));
    }
}
