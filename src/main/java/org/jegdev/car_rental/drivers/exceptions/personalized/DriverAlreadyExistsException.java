package org.jegdev.car_rental.drivers.exceptions.personalized;

import org.jegdev.car_rental.drivers.exceptions.base.DriverException;

public class DriverAlreadyExistsException extends DriverException {
    public DriverAlreadyExistsException(String documentId) {
        super("DRIVER-001", String.format("Ya existe un conductor con el numero de documento: %s", documentId));
    }
}
