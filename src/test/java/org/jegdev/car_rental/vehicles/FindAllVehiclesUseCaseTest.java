package org.jegdev.car_rental.vehicles;

import org.jegdev.car_rental.vehicles.application.usecase.FindAllVehiclesUseCase;
import org.jegdev.car_rental.vehicles.domain.model.Vehicle;
import org.jegdev.car_rental.vehicles.domain.model.VehicleStatus;
import org.jegdev.car_rental.vehicles.domain.model.VehicleType;
import org.jegdev.car_rental.vehicles.domain.repository.VehicleRepository;
import org.jegdev.car_rental.vehicles.exceptions.personalized.NoExistsVehiclesException;
import org.jegdev.car_rental.vehicles.infrastructure.dto.VehicleResponse;
import org.jegdev.car_rental.vehicles.infrastructure.mapper.VehicleDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Habilita las extensiones de Mockito para JUnit 5.
@ExtendWith(MockitoExtension.class)
class FindAllVehiclesUseCaseTest {

    // Inyecta los mocks en la clase de caso de uso.
    @InjectMocks
    private FindAllVehiclesUseCase findAllVehiclesUseCase;

    // Crea un mock del repositorio de vehículos.
    @Mock
    private VehicleRepository vehicleRepository;

    // Crea un mock del mapper de DTO a dominio.
    @Mock
    private VehicleDtoMapper vehicleDtoMapper;

    // Lista de vehículos de ejemplo para las pruebas.
    private List<Vehicle> vehicleList;

    // Lista de DTOs de respuesta de vehículos de ejemplo para las pruebas.
    private List<VehicleResponse> vehicleResponseList;

    // Configuración inicial que se ejecuta antes de cada prueba.
    @BeforeEach
    void setUp() {
        Vehicle vehicle1 = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .plate("ABC-123")
                .brand("Toyota")
                .model("Corolla")
                .type(VehicleType.CAR)
                .status(VehicleStatus.AVAILABLE)
                .year(2020)
                .dailyRate(100.0)
                .build();
        Vehicle vehicle2 = Vehicle.builder()
                .id(UUID.randomUUID().toString())
                .plate("DEF-456")
                .brand("Honda")
                .model("Civic")
                .type(VehicleType.CAR)
                .status(VehicleStatus.RENTED)
                .year(2022)
                .dailyRate(120.0)
                .build();
        vehicleList = List.of(vehicle1, vehicle2);

        VehicleResponse response1 = VehicleResponse.builder()
                .id(vehicle1.getId())
                .plate(vehicle1.getPlate())
                .brand(vehicle1.getBrand())
                .model(vehicle1.getModel())
                .type(vehicle1.getType())
                .status(vehicle1.getStatus())
                .year(vehicle1.getYear())
                .dailyRate(vehicle1.getDailyRate())
                .build();
        VehicleResponse response2 = VehicleResponse.builder()
                .id(vehicle2.getId())
                .plate(vehicle2.getPlate())
                .brand(vehicle2.getBrand())
                .model(vehicle2.getModel())
                .type(vehicle2.getType())
                .status(vehicle2.getStatus())
                .year(vehicle2.getYear())
                .dailyRate(vehicle2.getDailyRate())
                .build();
        vehicleResponseList = List.of(response1, response2);
    }

    /**
     * Prueba el caso de éxito donde se encuentran vehículos y se devuelven como DTOs.
     */
    @Test
    void findAllVehicles_shouldReturnList_whenVehiclesExist() {
        // ARRANGE: Configura el comportamiento de los mocks.
        // Simula que el repositorio devuelve una lista de vehículos.
        Mockito.when(vehicleRepository.findAll()).thenReturn(vehicleList);
        // Simula que el mapper convierte la lista de vehículos a una lista de DTOs.
        Mockito.when(vehicleDtoMapper.toResponseList(vehicleList)).thenReturn(vehicleResponseList);

        // ACT: Llama al método bajo prueba.
        List<VehicleResponse> result = findAllVehiclesUseCase.findAllVehicles();

        // ASSERT: Verifica el resultado.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(vehicleResponseList, result);

        // Verifica que los métodos esperados fueron llamados.
        Mockito.verify(vehicleRepository).findAll();
        Mockito.verify(vehicleDtoMapper).toResponseList(vehicleList);
    }

    /**
     * Prueba el escenario donde no se encuentran vehículos.
     * Se espera que se lance una NoExistsVehiclesException.
     */
    @Test
    void findAllVehicles_shouldThrowException_whenNoVehiclesExist() {
        // ARRANGE: Configura el comportamiento del mock para devolver una lista vacía.
        Mockito.when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(NoExistsVehiclesException.class, () -> findAllVehiclesUseCase.findAllVehicles());

        // Verifica que el método del mapper nunca se llamó.
        Mockito.verify(vehicleRepository).findAll();
        Mockito.verify(vehicleDtoMapper, Mockito.never()).toResponseList(Mockito.anyList());
    }
}

