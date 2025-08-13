package org.jegdev.car_rental.vehicles;

import org.jegdev.car_rental.vehicles.application.usecase.GetVehicleByPlateUseCase;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;
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
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GetVehicleByPlateUseCaseTest {

    @InjectMocks
    private GetVehicleByPlateUseCase getVehicleByPlateUseCase;

    @Mock
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .type(VehicleType.CAR)
                .status(VehicleStatus.AVAILABLE)
                .year(2020)
                .dailyRate(100.0)
                .build();
    }

    /**
     * Prueba el caso de éxito donde se encuentra un vehículo por su placa.
     */
    @Test
    void findVehicleByPlate_shouldReturnVehicle_whenVehicleExists() {
        // ARRANGE: Configura el mock para devolver un Optional con un vehículo.
        Mockito.when(vehicleRepository.findByPlate("ABC-123")).thenReturn(Optional.of(vehicle));

        // ACT: Llama al método bajo prueba.
        Vehicle result = getVehicleByPlateUseCase.findVehicleByPlate("ABC-123");

        // ASSERT: Verifica que el resultado no sea nulo y que tenga la placa correcta.
        Assertions.assertNotNull(result);
        Assertions.assertEquals("ABC-123", result.getPlate());

        // Verifica que el método del repositorio fue llamado.
        Mockito.verify(vehicleRepository).findByPlate("ABC-123");
    }

    /**
     * Prueba el escenario donde el vehículo no existe.
     * Se espera que se lance una VehicleNotFoundByPlateException.
     */
    @Test
    void findVehicleByPlate_shouldThrowException_whenVehicleDoesNotExist() {
        // ARRANGE: Configura el mock para devolver un Optional vacío.
        Mockito.when(vehicleRepository.findByPlate("DEF-456")).thenReturn(Optional.empty());

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(VehicleNotFoundByPlateException.class, () ->
                getVehicleByPlateUseCase.findVehicleByPlate("DEF-456")
        );

        // Verifica que el método del repositorio fue llamado.
        Mockito.verify(vehicleRepository).findByPlate("DEF-456");
    }
}
