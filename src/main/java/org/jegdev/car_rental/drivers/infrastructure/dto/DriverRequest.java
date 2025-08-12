package org.jegdev.car_rental.drivers.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class DriverRequest {
    @Schema(
            description = "Nombre del conductor",
            example = "Juan Pérez",
            required = true
    )
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Schema(
            description = "Número de documento del conductor",
            example = "123456789",
            required = true
    )
    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentId;

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
