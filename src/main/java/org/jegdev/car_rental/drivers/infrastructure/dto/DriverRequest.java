package org.jegdev.car_rental.drivers.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para recibir los datos de un conductor desde el cliente.
 * Contiene anotaciones para la validación de los datos de entrada.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "DriverRequest",
        description = "Representa los datos de entrada para la creación o actualización de un conductor en el sistema de alquiler de vehículos."
)
public class DriverRequest {

    @Schema(
            description = "El nombre completo del conductor.",
            example = "Ana Gómez",
            required = true
    )
    @NotBlank(message = "El nombre del conductor es obligatorio y no puede estar en blanco.")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
    private String name;

    @Schema(
            description = "Número de identificación único del conductor, como cédula o pasaporte.",
            example = "1020304050",
            required = true
    )
    @NotBlank(message = "El número de documento del conductor es obligatorio y no puede estar en blanco.")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres.")
    private String documentId;

    @Schema(
            description = "El número de teléfono del conductor, en formato internacional (p. ej., +57 3001234567).",
            example = "+57 3001234567",
            required = true
    )
    @NotBlank(message = "El número de teléfono del conductor es obligatorio y no puede estar en blanco.")
    @Size(min = 7, max = 20, message = "El número de teléfono debe tener entre 7 y 20 caracteres.")
    private String phoneNumber;

    @Schema(
            description = "El correo electrónico único del conductor para comunicaciones. Se valida el formato.",
            example = "ana.gomez@email.com",
            required = true
    )
    @NotBlank(message = "El correo electrónico del conductor es obligatorio y no puede estar en blanco.")
    @Email(message = "El formato del correo electrónico es inválido.")
    @Size(min = 5, max = 100, message = "El correo electrónico debe tener entre 5 y 100 caracteres.")
    private String email;
}