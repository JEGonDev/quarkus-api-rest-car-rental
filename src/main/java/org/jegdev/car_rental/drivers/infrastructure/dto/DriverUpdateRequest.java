package org.jegdev.car_rental.drivers.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Schema(
        name = "DriverUpdateRequest",
        description = "Representa los datos de entrada para la actualización de un conductor. " +
                "Los campos incluidos pueden ser parciales para operaciones de tipo PATCH."
)
public class DriverUpdateRequest {
    @Schema(
            description = "El nombre completo del conductor.",
            example = "Ana Gómez"
    )
    @NotBlank(message = "El nombre del conductor es obligatorio y no puede estar en blanco.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String name;

    @Schema(
            description = "El número de teléfono actualizado del conductor, en formato internacional.",
            example = "+57 3001234567"
    )
    @NotBlank(message = "El número de teléfono del conductor es obligatorio y no puede estar en blanco.")
    @Size(min = 7, max = 20, message = "El número de teléfono debe tener entre 7 y 20 caracteres.")
    private String phoneNumber;

    @Schema(
            description = "El nuevo correo electrónico del conductor. Se valida su formato.",
            example = "ana.gomez@email.com"
    )
    @NotBlank(message = "El correo electrónico del conductor es obligatorio y no puede estar en blanco.")
    @Email(message = "El formato del correo electrónico es inválido.")
    @Size(min = 5, max = 100, message = "El correo electrónico debe tener entre 5 y 100 caracteres.")
    private String email;
}