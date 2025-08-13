package org.jegdev.car_rental.rental.exceptions.base;

import org.jegdev.car_rental.shared.errors.ApiException;

public abstract class RentalException extends ApiException {
    protected RentalException(String code, int httpNotFound, String message) {
        super(code, message);
    }
}
