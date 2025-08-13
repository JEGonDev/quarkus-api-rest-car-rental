package org.jegdev.car_rental.rental.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.infrastructure.entity.RentalEntity;
import org.jegdev.car_rental.rental.infrastructure.mapper.RentalPersistenceMapper;

import java.util.Optional;

// Repositorio de implementación para la entidad Rental, utilizando Panache.
@ApplicationScoped
public class RentalRepositoryImpl implements RentalRepository {

    private static final Logger LOG = Logger.getLogger(RentalRepositoryImpl.class.getName());

    // PanacheRepository para la entidad Rental
    private final RentalPanacheRepository repository;
    // Mapper para convertir entre dominio y persistencia
    private final RentalPersistenceMapper mapper;

    @Inject
    public RentalRepositoryImpl(RentalPanacheRepository repository, RentalPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    public Rental save(Rental rental) {
        LOG.infof("Guardando nueva renta para vehículo: %s", rental.getVehicleId());
        RentalEntity entity = mapper.toEntity(rental);
        repository.persist(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    public Optional<Rental> findById(String id) {
        LOG.infof("Buscando renta por ID: %s", id);
        return repository.findByIdOptional(new org.bson.types.ObjectId(id))
                .map(mapper::toDomain);
    }

    /**
     * Encuentra una renta activa buscando por el ID del vehículo.
     *
     * @param vehicleId El ID del vehículo (placa) asociado a la renta.
     * @return Un Optional con la renta encontrada, si existe.
     */
    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    public Optional<Rental> findByVehicleId(String vehicleId) {
        LOG.infof("Buscando renta por vehicleId: %s", vehicleId);
        return repository.find("vehicleId", vehicleId)
                .firstResultOptional()
                .map(mapper::toDomain);
    }

    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    public void deleteByOrderId(String id) {
        LOG.infof("Eliminando renta con ID: %s", id);
        repository.deleteById(new org.bson.types.ObjectId(id));
        LOG.infof("Renta con ID: %s eliminada exitosamente.", id);
    }

    @Override
    @Retry(maxRetries = 3, delay = 2000)
    @Timeout(2000)
    public Rental update(Rental rental) {
        LOG.infof("Actualizando renta con ID: %s", rental.getId());
        RentalEntity entity = mapper.toEntity(rental);
        repository.update(entity);
        return mapper.toDomain(entity);
    }
}
