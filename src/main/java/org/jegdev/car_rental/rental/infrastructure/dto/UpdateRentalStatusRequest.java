package org.jegdev.car_rental.rental.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jegdev.car_rental.rental.domain.model.RentalStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRentalStatusRequest {
    @NotNull(message = "El nuevo estado no puede ser nulo")
    private RentalStatus status;
}
