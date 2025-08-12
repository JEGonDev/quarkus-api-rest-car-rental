package org.jegdev.car_rental.drivers.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

// Esta clase representa el modelo de negocio de un conductor.

@Data
@AllArgsConstructor
@Builder
public class Driver {
    private String id;
    private String name;
    private String documentId;
    private String phoneNumber;
    private String email;
}
