package org.jegdev.car_rental.drivers.infrastructure.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.infrastructure.entity.DriverEntity;

/**
 * Mapper encargado de convertir entre entidades de persistencia
 * y objetos del modelo de dominio (Driver).
 *
 * Este mapper es responsable de transformar los datos almacenados en la base de datos
 * en objetos del dominio que puedan ser utilizados por la lógica de negocio.
 */
@ApplicationScoped
public class DriverPersistenceMapper {

    /**
     * Convierte una entidad de persistencia DriverEntity a un objeto de dominio Driver.
     *
     * @param entity entidad a convertir
     * @return objeto de dominio Driver
     */
    public Driver toDomain(DriverEntity entity) {
        if (entity == null) {
            return null;
        }

        return Driver.builder()
                .id(entity.id != null ? entity.id.toHexString() : null)
                .name(entity.getName())
                .documentId(entity.getDocumentId())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .build();
    }

    /**
     * Convierte un objeto de dominio Driver a una entidad de persistencia DriverEntity.
     *
     * @param driver objeto de dominio a persistir
     * @return entidad preparada para guardar en base de datos
     */
    public DriverEntity toEntity(Driver driver) {
        if (driver == null) {
            return null;
        }

        DriverEntity entity = DriverEntity.builder()
                .name(driver.getName())
                .documentId(driver.getDocumentId())
                .phoneNumber(driver.getPhoneNumber())
                .email(driver.getEmail())
                .build();

        // Asigna el ObjectId si el objeto de dominio tiene un ID
        if (driver.getId() != null) {
            entity.id = new ObjectId(driver.getId());
        }

        return entity;
    }
}
