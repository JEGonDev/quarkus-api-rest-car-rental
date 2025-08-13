package org.jegdev.car_rental.rental.exceptions.base;

import org.jegdev.car_rental.shared.errors.ApiException;

public abstract class RentalExceptions extends ApiException {
    protected RentalExceptions(String code, int httpNotFound, String message) {
        super(code, message);
    }
}
