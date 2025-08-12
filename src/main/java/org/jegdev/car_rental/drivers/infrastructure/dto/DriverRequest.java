package org.jegdev.car_rental.drivers.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentId;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String phoneNumber;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;
}
