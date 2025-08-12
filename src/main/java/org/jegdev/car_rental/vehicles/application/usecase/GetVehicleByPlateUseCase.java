package org.jegdev.car_rental.vehicles.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;

@ApplicationScoped // Se crea una instancia única de este caso de uso
public class GetVehicleByPlateUseCase {

    private static final Logger LOG = Logger.getLogger(GetVehicleByPlateUseCase.class.getName());

    private final VehicleRepository vehicleRepository; // Repositorio para acceder a los vehículos

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param vehicleRepository Repositorio de vehículos para acceder a los datos.
     */
    @Inject
    public GetVehicleByPlateUseCase(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Busca y devuelve un vehículo por su placa.
     *
     * @param plate La placa del vehículo a buscar.
     * @return El vehículo encontrado.
     * @throws VehicleNotFoundByPlateException si no se encuentra el vehículo.
     */
    public Vehicle findVehicleByPlate(String plate) {
        LOG.infof("Iniciando la búsqueda de un vehículo con placa: %s", plate);

        return vehicleRepository.findByPlate(plate)
                .map(vehicle -> {
                    LOG.debugf("Vehículo con placa '%s' encontrado exitosamente.", plate);
                    return vehicle;
                })
                .orElseThrow(() -> {
                    LOG.warnf("No se encontró ningún vehículo con la placa: %s", plate);
                    return new VehicleNotFoundByPlateException(plate);
                });
    }
}
