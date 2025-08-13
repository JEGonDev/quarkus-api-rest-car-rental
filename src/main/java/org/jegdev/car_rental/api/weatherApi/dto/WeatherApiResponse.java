package org.jegdev.car_rental.api.weatherApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Objeto de transferencia de datos (DTO) para la respuesta de WeatherAPI.com
 *
 * Esta clase mapea la estructura JSON completa que devuelve WeatherAPI en sus
 * endpoints de clima actual. Utiliza Jackson para la deserialización automática
 * y está configurada para ignorar propiedades desconocidas para mantener
 * compatibilidad con futuras versiones del API.
 *
 * La estructura de la respuesta incluye información de ubicación y condiciones
 * meteorológicas actuales con todos los detalles necesarios para el sistema
 * de alquiler de carros.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Respuesta completa del servicio WeatherAPI.com")
public class WeatherApiResponse {

    /** Información de la ubicación consultada */
    @Schema(description = "Información detallada de la ubicación")
    public Location location;

    /** Condiciones meteorológicas actuales */
    @Schema(description = "Condiciones meteorológicas actuales")
    public Current current;

    /**
     * Información detallada de la ubicación geográfica.
     *
     * Contiene datos como nombre de la ciudad, región, país y hora local
     * según la respuesta de WeatherAPI.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Información de ubicación geográfica")
    public static class Location {

        /** Nombre de la ciudad o ubicación */
        @Schema(description = "Nombre de la ciudad", example = "Bogota")
        public String name;

        /** Región, estado o departamento */
        @Schema(description = "Región, estado o departamento", example = "Cundinamarca")
        public String region;

        /** Nombre del país */
        @Schema(description = "Nombre del país", example = "Colombia")
        public String country;

        /** Fecha y hora local en la ubicación consultada */
        @JsonProperty("localtime")
        @Schema(description = "Fecha y hora local", example = "2025-01-20 14:30")
        public String localtime;
    }

    /**
     * Condiciones meteorológicas actuales de la ubicación.
     *
     * Incluye temperatura, condiciones atmosféricas, viento, humedad
     * y timestamp de la última actualización de los datos.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Condiciones meteorológicas actuales")
    public static class Current {

        /** Temperatura actual en grados Celsius */
        @JsonProperty("temp_c")
        @Schema(description = "Temperatura en grados Celsius", example = "18.5")
        public Double tempC;

        /** Temperatura actual en grados Fahrenheit */
        @JsonProperty("temp_f")
        @Schema(description = "Temperatura en grados Fahrenheit", example = "65.3")
        public Double tempF;

        /** Descripción detallada de las condiciones atmosféricas */
        @Schema(description = "Condiciones atmosféricas detalladas")
        public Condition condition;

        /** Velocidad del viento en kilómetros por hora */
        @JsonProperty("wind_kph")
        @Schema(description = "Velocidad del viento en km/h", example = "12.5")
        public Double windKph;

        /** Porcentaje de humedad relativa */
        @Schema(description = "Porcentaje de humedad", example = "75")
        public Integer humidity;

        /** Timestamp de la última actualización de los datos */
        @JsonProperty("last_updated")
        @Schema(description = "Última actualización de datos", example = "2025-01-20 14:15")
        public String lastUpdated;
    }

    /**
     * Descripción detallada de las condiciones atmosféricas.
     *
     * Proporciona texto descriptivo, icono representativo y código numérico
     * para las condiciones meteorológicas actuales.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Schema(description = "Descripción de condiciones atmosféricas")
    public static class Condition {

        /** Descripción textual de las condiciones (ej: "Parcialmente nublado") */
        @Schema(description = "Descripción textual del clima", example = "Partly cloudy")
        public String text;

        /** URL relativa del icono representativo del clima */
        @Schema(description = "URL del icono del clima", example = "//cdn.weatherapi.com/weather/64x64/day/116.png")
        public String icon;

        /** Código numérico único para el tipo de condición meteorológica */
        @Schema(description = "Código numérico de la condición", example = "1003")
        public Integer code;
    }
}