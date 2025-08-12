package org.jegdev.car_rental.vehicles.exceptions.personalized;

import org.jegdev.car_rental.vehicles.exceptions.base.VehicleException;

public class NoExistsVehiclesException extends VehicleException {
    public NoExistsVehiclesException() {
        super("VEHICLE-003", "No existen vehículos disponibles en el sistema");
    }
}
