package org.jegdev.car_rental.drivers;

import org.jegdev.car_rental.drivers.application.usecase.CreateDriverUseCase;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverAlreadyExistsException;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverRequest;
import org.jegdev.car_rental.drivers.infrastructure.mapper.DriverDtoMapper;
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

// Habilita las extensiones de Mockito para JUnit 5.
@ExtendWith(MockitoExtension.class)
public class CreateDriverUseCaseTest {

    // Inyecta los mocks en la clase de caso de uso.
    @InjectMocks
    private CreateDriverUseCase createDriverUseCase;

    // Crea un mock del repositorio de conductores.
    @Mock
    private DriverRepository driverRepository;

    // Crea un mock del mapper de DTO a dominio.
    @Mock
    private DriverDtoMapper driverDtoMapper;

    // Objeto DTO de ejemplo para usar en las pruebas.
    private DriverRequest driverRequest;

    // Entidad de dominio de ejemplo para usar en las pruebas.
    private Driver driver;

    // Configuración inicial que se ejecuta antes de cada prueba.
    @BeforeEach
    void setUp() {
        driverRequest = DriverRequest.builder()
                .name("Carlos Rodriguez")
                .documentId("1020304050")
                .phoneNumber("+573001234567")
                .email("carlos.rodriguez@email.com")
                .build();

        driver = Driver.builder()
                .id(UUID.randomUUID().toString())
                .name("Carlos Rodriguez")
                .documentId("1020304050")
                .phoneNumber("+573001234567")
                .email("carlos.rodriguez@email.com")
                .build();
    }

    /**
     * Prueba el caso de éxito donde se crea un conductor correctamente.
     * 1. El conductor no existe previamente.
     * 2. Se mapea el DTO a la entidad de dominio.
     * 3. Se guarda el conductor en la base de datos.
     */
    @Test
    void createDriver_shouldCreateDriver_whenDriverDoesNotExist() {
        // ARRANGE: Configura el comportamiento de los mocks.
        // Simula que el conductor no existe en la base de datos.
        Mockito.when(driverRepository.findByDocumentId(driverRequest.getDocumentId())).thenReturn(Optional.empty());
        // Simula el mapeo del DTO al objeto de dominio.
        Mockito.when(driverDtoMapper.toDomain(driverRequest)).thenReturn(driver);
        // Simula que el repositorio guarda y devuelve el objeto.
        Mockito.when(driverRepository.save(driver)).thenReturn(driver);

        // ACT: Llama al método bajo prueba.
        Driver result = createDriverUseCase.createDriver(driverRequest);

        // ASSERT: Verifica que los métodos esperados fueron llamados y el resultado es correcto.
        // Verifica que se llamó a findByDocumentId para validar la existencia.
        Mockito.verify(driverRepository).findByDocumentId(driverRequest.getDocumentId());
        // Verifica que se llamó al mapper para convertir el DTO.
        Mockito.verify(driverDtoMapper).toDomain(driverRequest);
        // Verifica que se llamó a save para guardar el conductor.
        Mockito.verify(driverRepository).save(driver);
        // Comprueba que el objeto devuelto no es nulo y tiene un ID.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(driver.getId(), result.getId());
    }

    /**
     * Prueba el escenario donde se intenta crear un conductor que ya existe.
     * Se espera que se lance una DriverAlreadyExistsException.
     */
    @Test
    void createDriver_shouldThrowException_whenDriverAlreadyExists() {
        // ARRANGE: Configura el comportamiento de los mocks.
        // Simula que el conductor ya existe en la base de datos.
        Mockito.when(driverRepository.findByDocumentId(driverRequest.getDocumentId())).thenReturn(Optional.of(driver));

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(DriverAlreadyExistsException.class, () -> createDriverUseCase.createDriver(driverRequest));

        // Verifica que el método de guardado nunca se llamó.
        Mockito.verify(driverRepository, Mockito.never()).save(Mockito.any(Driver.class));
    }
}
