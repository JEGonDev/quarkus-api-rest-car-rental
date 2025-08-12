package org.jegdev.car_rental.vehicles.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.infrastructure.entity.VehicleEntity;

/**
 * Mapper encargado de convertir entre entidades de persistencia
 * y objetos del modelo de dominio (Vehicle).
 *
 * Este mapper es responsable de transformar los datos almacenados en la base de datos
 * en objetos del dominio que puedan ser utilizados por la lógica de negocio.
 */
@ApplicationScoped // Se crea una unica instancia para todo el ciclo de vida de la App
public class VehiclePersistenceMapper {

    /**
     * Convierte un objeto de dominio Vehicle a una entidad de persistencia VehicleEntity.
     *
     * @param entity objeto de dominio a persistir
     * @return entidad preparada para guardar en base de datos
     */
    public Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) {
            return null;  // Manejo del caso nulo para evitar NullPointerException
        }

        return Vehicle.builder()
                .id(entity.id != null ? entity.id.toHexString() : null) // Convierte ObjectId a String si no es nulo
                .type(entity.getType())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .plate(entity.getPlate())
                .year(entity.getYear())
                .status(entity.getStatus())
                .dailyRate(entity.getDailyRate())
                .build();
    }

    public VehicleEntity toEntity(Vehicle vehicle) {
        // Primer paso contruir la entidad con todos los campos salvo el id
        VehicleEntity entity = VehicleEntity.builder()
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .plate(vehicle.getPlate())
                .year(vehicle.getYear())
                .status(vehicle.getStatus())
                .dailyRate(vehicle.getDailyRate())
                .build();

        // Segundo paso , si el dominio trae un id , se asigna directamente al campo publico heredado
        if (vehicle.getId() != null) {
            entity.id = new ObjectId(vehicle.getId());
        }

        return entity;
    }
}
