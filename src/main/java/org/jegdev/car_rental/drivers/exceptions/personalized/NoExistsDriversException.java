package org.jegdev.car_rental.drivers.exceptions.personalized;

public class NoExistsDriversException extends DriverAlreadyExistsException{
    public NoExistsDriversException() {
        super("No existen conductores disponibles en el sistema");
    }
}
