package org.jegdev.car_rental.vehicles.exceptions.base;

import org.jegdev.car_rental.shared.errors.ApiException;

public abstract class VehicleException extends ApiException {

    protected VehicleException(String code, String message) {
        super(code, message);
    }
}
