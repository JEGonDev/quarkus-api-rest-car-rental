package org.jegdev.car_rental.api.weatherApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Objeto de transferencia de datos simplificado para información meteorológica.
 *
 * Esta clase representa una versión simplificada y optimizada de la información
 * meteorológica que necesita el sistema de alquiler de carros. Contiene solo
 * los datos esenciales extraídos de la respuesta completa de WeatherAPI.
 *
 * Es la estructura que se devuelve en los endpoints públicos del sistema,
 * proporcionando información clara y concisa sobre las condiciones meteorológicas
 * actuales de cualquier ubicación.
 *
 * Características:
 * - Estructura simplificada para facilitar el consumo por parte del frontend
 * - Información esencial para decisiones relacionadas con alquiler de vehículos
 * - Compatible con serialización/deserialización JSON automática
 * - Documentación OpenAPI integrada para generación de schemas
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Información meteorológica simplificada para el sistema de alquiler")
public class WeatherInfo {

    /**
     * Nombre completo de la ubicación en formato legible.
     * Generalmente incluye ciudad, región/estado y país separados por comas.
     *
     * Ejemplo: "Bogotá, Cundinamarca, Colombia"
     */
    @Schema(
            description = "Nombre completo de la ubicación",
            example = "Bogotá, Cundinamarca, Colombia",
            required = true
    )
    public String locationName;

    /**
     * Temperatura actual en grados Celsius.
     * Valor decimal que permite precisión en las mediciones de temperatura.
     *
     * Útil para que los usuarios puedan tomar decisiones informadas sobre
     * el tipo de vehículo a alquilar (aire acondicionado, tipo de combustible, etc.)
     */
    @Schema(
            description = "Temperatura actual en grados Celsius",
            example = "18.5",
            required = true
    )
    public Double tempC;

    /**
     * Descripción textual de las condiciones meteorológicas actuales.
     * Proporciona una descripción en lenguaje natural del estado del clima.
     *
     * Ejemplos: "Soleado", "Parcialmente nublado", "Lluvia ligera", etc.
     */
    @Schema(
            description = "Descripción textual de las condiciones meteorológicas",
            example = "Partly cloudy",
            required = true
    )
    public String conditionText;

    /**
     * URL del icono representativo de las condiciones meteorológicas.
     * URL relativa que puede requerir prefijo del dominio de WeatherAPI para uso completo.
     *
     * Permite mostrar representaciones visuales del clima en la interfaz de usuario.
     */
    @Schema(
            description = "URL del icono del clima (relativa)",
            example = "//cdn.weatherapi.com/weather/64x64/day/116.png"
    )
    public String iconUrl;

    /**
     * Velocidad del viento en kilómetros por hora.
     * Información importante para evaluar condiciones de conducción.
     *
     * Especialmente relevante para decisiones sobre alquiler de vehículos
     * grandes, motocicletas o vehículos descapotables.
     */
    @Schema(
            description = "Velocidad del viento en kilómetros por hora",
            example = "12.5"
    )
    public Double windKph;

    /**
     * Porcentaje de humedad relativa del aire.
     * Valor entre 0 y 100 que indica la cantidad de humedad en el ambiente.
     *
     * Puede influir en el comfort del viaje y la necesidad de
     * vehículos con sistemas de climatización específicos.
     */
    @Schema(
            description = "Porcentaje de humedad relativa",
            example = "75",
            minimum = "0",
            maximum = "100"
    )
    public Integer humidity;

    /**
     * Fecha y hora de la última actualización de los datos meteorológicos.
     * Timestamp que permite verificar la frescura de la información.
     *
     * Importante para mostrar al usuario qué tan actualizada está la información
     * meteorológica que está consultando.
     */
    @Schema(
            description = "Fecha y hora de última actualización de los datos",
            example = "2025-01-20 14:15"
    )
    public String lastUpdated;
}