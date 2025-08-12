package org.jegdev.car_rental.drivers.exceptions.personalized;

import org.jegdev.car_rental.drivers.exceptions.base.DriverException;

public class NoExistsDriversException extends DriverException {
    public NoExistsDriversException() {
        super("DRIVER-003", "No existen conductores disponibles en el sistema");
    }
}
