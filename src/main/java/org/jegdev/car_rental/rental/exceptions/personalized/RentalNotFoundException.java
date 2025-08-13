package org.jegdev.car_rental.rental.exceptions.personalized;

import org.jegdev.car_rental.rental.exceptions.base.RentalException;

import java.net.HttpURLConnection;

public class RentalNotFoundException extends RentalException {
    public RentalNotFoundException(String rentalId) {
        super(String.format("No se encontró la renta con ID: %s", rentalId),
                HttpURLConnection.HTTP_NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }
}
