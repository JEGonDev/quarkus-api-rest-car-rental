package org.jegdev.car_rental.rental.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalResponse {
    private String id;
    private String vehicleId;
    private String driverId;
    private Instant startDate;
    private Instant endDate;
    private String origin;
    private String destination;
    private double price;
    private RentalStatus status;
    private Instant createdAt;
}
