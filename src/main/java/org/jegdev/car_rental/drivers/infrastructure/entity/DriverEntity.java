package org.jegdev.car_rental.drivers.infrastructure.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;

/**
 * Entidad de persistencia para la colección 'drivers'.
 * Hereda de PanacheMongoEntity para obtener un campo 'id' de tipo ObjectId
 * y métodos de Panache para la interacción con la base de datos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "drivers")
public class DriverEntity extends PanacheMongoEntity {
    private String name;
    private String documentId;
    private String phoneNumber;
    private String email;
}
