package org.jegdev.car_rental.vehicles.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleRequest;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;

/**
 * Mapper para convertir entre VehicleRequest (DTO de entrada) y Vehicle (modelo de dominio).
 *
 * Este mapper es responsable de transformar los datos recibidos desde el cliente
 * en un objeto del dominio que pueda ser utilizado por la lógica de negocio.
 */
@ApplicationScoped // Se crea una unica instancia para todo el ciclo de vida de la App
public class VehicleDtoMapper {

    /**
     * Convierte un VehicleRequest (DTO de entrada) a un Vehicle (modelo de dominio).
     *
     * @param request objeto recibido desde el cliente
     * @return instancia de Vehicle para uso en el dominio
     */
    public Vehicle toDomain(VehicleRequest request) {
        return Vehicle.builder()
                .type(request.getType())
                .brand(request.getBrand())
                .model(request.getModel())
                .plate(request.getPlate())
                .year(request.getYear())
                .status(request.getStatus())
                .dailyRate(request.getDailyRate())
                .build();
    }

    /**
     * Convierte un Vehicle (modelo de dominio) a un VehicleResponse (DTO de salida).
     *
     * @param vehicle objeto de dominio procesado internamente
     * @return respuesta formateada para enviar al cliente
     */
    public VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .plate(vehicle.getPlate())
                .year(vehicle.getYear())
                .status(vehicle.getStatus())
                .dailyRate(vehicle.getDailyRate())
                .build();
    }
}
