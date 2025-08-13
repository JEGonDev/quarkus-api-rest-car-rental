package org.jegdev.car_rental.rental.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {
    private String id; // Identificador único de la renta
    private String vehicleId; // Identificador del vehículo alquilado (Placa)
    private String driverId; // Identificador del conductor que alquila el vehículo (Documento de identidad)
    private Instant startDate; // Fecha y hora de inicio de la renta
    private Instant endDate; // Fecha y hora de finalización de la renta
    private String origin; // Lugar de origen de la renta (Ciudad o ubicación de recogida)
    private String destination; // Lugar de destino de la renta (Ciudad o ubicación de entrega)
    private double price; // Precio total de la renta en la moneda local
    private RentalStatus status; // Estado actual de la renta
    private Instant createdAt; // Marca de tiempo de creación de la renta
}
