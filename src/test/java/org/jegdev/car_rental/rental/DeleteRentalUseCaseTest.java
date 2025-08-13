package org.jegdev.car_rental.rental;

import org.jegdev.car_rental.rental.application.usecase.DeleteRentalUseCase;
import org.jegdev.car_rental.rental.domain.model.Rental;
import org.jegdev.car_rental.rental.domain.repository.RentalRepository;
import org.jegdev.car_rental.rental.exceptions.personalized.RentalNotFoundException;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.VehicleNotFoundByPlateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DeleteRentalUseCaseTest {

    // Inyecta los mocks en la clase de caso de uso.
    @InjectMocks
    private DeleteRentalUseCase deleteRentalUseCase;

    // Crea un mock del repositorio de rentas.
    @Mock
    private RentalRepository rentalRepository;

    // Crea un mock del repositorio de vehículos.
    @Mock
    private VehicleRepository vehicleRepository;

    // Define un ID de renta y un ID de vehículo para usar en las pruebas.
    private static final String RENTAL_ID = "rental-abc-123";
    private static final String VEHICLE_ID = "vehicle-xyz-456";

    // Configuración inicial que se ejecuta antes de cada prueba.
    @BeforeEach
    void setUp() {
        // Inicializa los mocks, no es estrictamente necesario con @ExtendWith,
        // pero es una buena práctica en algunos casos.
    }

    /**
     * Prueba el caso de éxito donde una renta se elimina correctamente.
     * 1. Se encuentra la renta.
     * 2. Se encuentra el vehículo.
     * 3. Se actualiza el estado del vehículo a DISPONIBLE.
     * 4. Se elimina la renta.
     */
    @Test
    void deleteRental_shouldDeleteRentalAndUpdateVehicleStatus_whenRentalExists() {
        // ARRANGE: Configura el comportamiento de los mocks.
        Rental mockRental = Mockito.mock(Rental.class);
        Mockito.when(mockRental.getVehicleId()).thenReturn(VEHICLE_ID);

        Vehicle mockVehicle = Mockito.mock(Vehicle.class);
        // Los 'stubbings' de getId() y getStatus() fueron eliminados porque no se usan.

        Mockito.when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(mockRental));
        Mockito.when(vehicleRepository.findByPlate(VEHICLE_ID)).thenReturn(Optional.of(mockVehicle));

        // ACT: Llama al método bajo prueba.
        deleteRentalUseCase.deleteRental(RENTAL_ID);

        // ASSERT: Verifica que los métodos esperados fueron llamados.
        // Verifica que se llamó a findById en el repositorio de rentas.
        Mockito.verify(rentalRepository).findById(RENTAL_ID);
        // Verifica que se llamó a findById en el repositorio de vehículos.
        Mockito.verify(vehicleRepository).findByPlate(VEHICLE_ID);
        // Verifica que se llamó a setStatus en el vehículo con el estado correcto.
        Mockito.verify(mockVehicle).setStatus(VehicleStatus.AVAILABLE);
        // Verifica que se llamó a update en el repositorio de vehículos con el mock correcto.
        Mockito.verify(vehicleRepository).update(mockVehicle);
        // Verifica que se llamó a deleteById en el repositorio de rentas.
        Mockito.verify(rentalRepository).deleteByOrderId(RENTAL_ID);
    }

    /**
     * Prueba el escenario donde la renta no se encuentra.
     * Se espera que se lance una RentalNotFoundException.
     */
    @Test
    void deleteRental_shouldThrowException_whenRentalNotFound() {
        // ARRANGE: Configura el repositorio de rentas para que devuelva un Optional vacío.
        Mockito.when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(RentalNotFoundException.class, () -> deleteRentalUseCase.deleteRental(RENTAL_ID));

        // Verifica que el método de eliminación nunca se llamó.
        Mockito.verify(rentalRepository, Mockito.never()).deleteByOrderId(Mockito.anyString());
        // Verifica que el repositorio de vehículos nunca fue tocado.
        Mockito.verify(vehicleRepository, Mockito.never()).findByPlate(Mockito.anyString());
    }

    /**
     * Prueba el escenario donde la renta existe, pero el vehículo asociado no se encuentra.
     * Se espera que se lance una VehicleNotFoundException.
     */
    @Test
    void deleteRental_shouldThrowException_whenVehicleNotFound() {
        // ARRANGE: Configura el repositorio de rentas para que devuelva una renta
        // y el repositorio de vehículos para que no devuelva nada.
        Rental mockRental = Mockito.mock(Rental.class);
        Mockito.when(mockRental.getVehicleId()).thenReturn(VEHICLE_ID);

        Mockito.when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(mockRental));
        Mockito.when(vehicleRepository.findByPlate(VEHICLE_ID)).thenReturn(Optional.empty());

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(VehicleNotFoundByPlateException.class, () -> deleteRentalUseCase.deleteRental(RENTAL_ID));

        // Verifica que el método de eliminación de la renta nunca se llamó.
        Mockito.verify(rentalRepository, Mockito.never()).deleteByOrderId(Mockito.anyString());
    }
}