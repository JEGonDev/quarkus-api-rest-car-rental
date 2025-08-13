package org.jegdev.car_rental.rental.exceptions.personalized;

import org.jegdev.car_rental.rental.exceptions.base.RentalException;

import java.net.HttpURLConnection;

public class VehicleNotAvailableException extends RentalException {

    /**
     * Constructor que crea una instancia de la excepción.
     *
     * @param vehiclePlate La matrícula del vehículo que no está disponible,
     * la cual se incluye en el mensaje de error para mayor claridad.
     */
    public VehicleNotAvailableException(String vehiclePlate) {
        // Se llama al constructor de la clase base con un mensaje de error descriptivo,
        // el código de estado HTTP 409 (Conflict), y un código de error único.
        super(String.format("El vehículo con matrícula %s no está disponible para ser rentado.", vehiclePlate),
                HttpURLConnection.HTTP_CONFLICT,
                "VEHICLE_NOT_AVAILABLE");
    }
}
