package org.jegdev.car_rental;

import jakarta.ws.rs.ApplicationPath;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Clase que define la configuración global de la API con OpenAPI.
 * Proporciona metadatos importantes como el título, la versión y la información de contacto.
 */
@OpenAPIDefinition(
        tags = {
                @Tag(name = "Drivers", description = "Operaciones relacionadas con los conductores."),
                @Tag(name = "Vehicles", description = "Operaciones relacionadas con los vehículos."),
                @Tag(name = "Rental", description = "Operaciones relacionadas con las reservas de alquiler.")
        },
        info = @Info(
                title = "API de Gestión de Alquiler de Vehículos",
                version = "1.0.0",
                description = "Esta API proporciona endpoints para la gestión de conductores, vehículos y reservas en el sistema de alquiler.",
                contact = @Contact(
                        name = "JEGonDev",
                        url = "https://github.com/JEGonDev"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Entorno de desarrollo local")
        }
)
@ApplicationPath("/api/v1")
public class CarRentalApplication {
}
