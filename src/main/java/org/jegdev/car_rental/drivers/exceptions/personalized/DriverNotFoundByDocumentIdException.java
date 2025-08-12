package org.jegdev.car_rental.drivers.exceptions.personalized;

import org.jegdev.car_rental.drivers.exceptions.base.DriverException;

public class DriverNotFoundByDocumentIdException extends DriverException {
    public DriverNotFoundByDocumentIdException(String documentId) {
        super("DRIVER-002", String.format("No se encontró un conductor con el número de documento: %s", documentId));
    }
}
