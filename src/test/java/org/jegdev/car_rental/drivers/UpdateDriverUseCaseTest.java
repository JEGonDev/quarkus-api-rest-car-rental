package org.jegdev.car_rental.drivers;

import org.jegdev.car_rental.drivers.application.usecase.UpdateDriverUseCase;
import org.jegdev.car_rental.drivers.domain.model.Driver;
import org.jegdev.car_rental.drivers.domain.repository.DriverRepository;
import org.jegdev.car_rental.drivers.exceptions.personalized.DriverNotFoundByDocumentIdException;
import org.jegdev.car_rental.drivers.infrastructure.dto.DriverUpdateRequest;
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
public class UpdateDriverUseCaseTest {

    // Inyecta los mocks en la clase de caso de uso.
    @InjectMocks
    private UpdateDriverUseCase updateDriverUseCase;

    // Crea un mock del repositorio de conductores.
    @Mock
    private DriverRepository driverRepository;

    // Crea un mock del mapper de DTO a dominio.
    @Mock
    private DriverDtoMapper driverDtoMapper;

    // Objeto DTO de ejemplo para usar en las pruebas.
    private DriverUpdateRequest driverUpdateRequest;

    // Entidad de dominio de ejemplo que existe en la base de datos.
    private Driver existingDriver;

    // Entidad de dominio de ejemplo que representa el resultado actualizado.
    private Driver updatedDriver;

    // Configuración inicial que se ejecuta antes de cada prueba.
    @BeforeEach
    void setUp() {
        // Objeto DTO con los datos que se enviarán para la actualización.
        driverUpdateRequest = DriverUpdateRequest.builder()
                .name("Carlos R. A.")
                .phoneNumber("+573109876543")
                .email("carlos.update@email.com")
                .build();

        // Conductor que "existe" en la base de datos antes de la actualización.
        existingDriver = Driver.builder()
                .id(UUID.randomUUID().toString())
                .name("Carlos Rodriguez")
                .documentId("1020304050")
                .phoneNumber("+573001234567")
                .email("carlos.rodriguez@email.com")
                .build();

        // Conductor esperado después de la actualización.
        updatedDriver = Driver.builder()
                .id(existingDriver.getId())
                .name("Carlos R. A.")
                .documentId("1020304050")
                .phoneNumber("+573109876543")
                .email("carlos.update@email.com")
                .build();
    }

    /**
     * Prueba el caso de éxito donde se actualiza un conductor correctamente.
     * Se verifica que se busca el conductor, se actualizan sus datos y se guarda.
     */
    @Test
    void updateDriver_shouldUpdateDriver_whenDriverExists() {
        // ARRANGE: Configura el comportamiento de los mocks.
        // Simula que el conductor existe en la base de datos.
        Mockito.when(driverRepository.findByDocumentId(existingDriver.getDocumentId())).thenReturn(Optional.of(existingDriver));
        // Simula el mapeo del DTO a un objeto de dominio con los nuevos datos.
        Mockito.when(driverDtoMapper.toDomain(driverUpdateRequest)).thenReturn(updatedDriver);
        // Simula que el repositorio guarda y devuelve el objeto actualizado.
        Mockito.when(driverRepository.update(updatedDriver)).thenReturn(updatedDriver);

        // ACT: Llama al método bajo prueba.
        Driver result = updateDriverUseCase.updateDriverByDocumentId(existingDriver.getDocumentId(), driverUpdateRequest);

        // ASSERT: Verifica que los métodos esperados fueron llamados y el resultado es correcto.
        // Se verifica que se llamó a findByDocumentId para validar la existencia.
        Mockito.verify(driverRepository).findByDocumentId(existingDriver.getDocumentId());
        // Se verifica que se llamó al mapper para convertir el DTO.
        Mockito.verify(driverDtoMapper).toDomain(driverUpdateRequest);
        // Se verifica que se llamó a update para guardar el conductor.
        Mockito.verify(driverRepository).update(updatedDriver);
        // Se comprueba que el objeto devuelto no es nulo y tiene los datos actualizados.
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updatedDriver.getName(), result.getName());
        Assertions.assertEquals(updatedDriver.getPhoneNumber(), result.getPhoneNumber());
        Assertions.assertEquals(updatedDriver.getEmail(), result.getEmail());
    }

    /**
     * Prueba el escenario donde se intenta actualizar un conductor que no existe.
     * Se espera que se lance una DriverNotFoundByDocumentIdException.
     */
    @Test
    void updateDriver_shouldThrowException_whenDriverDoesNotExist() {
        // ARRANGE: Configura el comportamiento de los mocks.
        // Simula que el conductor no existe en la base de datos.
        Mockito.when(driverRepository.findByDocumentId(existingDriver.getDocumentId())).thenReturn(Optional.empty());

        // ACT & ASSERT: Verifica que se lance la excepción correcta.
        Assertions.assertThrows(DriverNotFoundByDocumentIdException.class, () ->
                updateDriverUseCase.updateDriverByDocumentId(existingDriver.getDocumentId(), driverUpdateRequest)
        );

        // Verifica que el método de actualización nunca se llamó.
        Mockito.verify(driverRepository, Mockito.never()).update(Mockito.any(Driver.class));
    }
}
