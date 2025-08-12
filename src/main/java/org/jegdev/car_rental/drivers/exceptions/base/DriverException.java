package org.jegdev.car_rental.drivers.exceptions.base;

import org.jegdev.car_rental.shared.errors.ApiException;

public abstract class DriverException extends ApiException {
    protected DriverException(String code, String message) {
        super(code, message);
    }
}
