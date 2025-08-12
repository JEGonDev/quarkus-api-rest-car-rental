package org.jegdev.car_rental.drivers.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO para la solicitud de actualización de un conductor.
 * Se utiliza para operaciones de PUT/PATCH donde no todos los campos son obligatorios.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequest {
    @Schema(
            description = "Nombre del conductor",
            example = "Juan Pérez",
            required = true
    )
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Schema(
            description = "Número de teléfono del conductor",
            example = "+57 3001234567",
            required = true
    )
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String phoneNumber;

    @Schema(
            description = "Correo electrónico del conductor",
            example = "juanitozaza@gmail.com"
    )
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;
}