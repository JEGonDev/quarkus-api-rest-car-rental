package org.jegdev.car_rental.api.rickAdnMorty.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jegdev.car_rental.api.rickAdnMorty.client.RickAndMortyApi;
import org.jegdev.car_rental.api.rickAdnMorty.dto.RickAndMortyResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Esta clase es un scheduler que llama a la API de Rick and Morty.
 * Se ejecuta automáticamente cada 5 minutos.
 */
@ApplicationScoped
public class RickAndMortyScheduler {

    /**
     * Inyectamos el cliente REST que creamos anteriormente.
     * La anotación @RestClient es necesaria para la inyección.
     */
    @Inject
    @RestClient
    RickAndMortyApi rickAndMortyApi;

    // Usamos un logger para registrar mensajes en la consola.
    private static final Logger LOG = Logger.getLogger(RickAndMortyScheduler.class);

    // Guardamos el número total de personajes para no consultarlo cada vez.
    private int totalCharacters = 0;

    /**
     * Este método se ejecuta automáticamente cada 5 minutos.
     */
    @Scheduled(every = "5m")
    void updateCharacters() {
        LOG.info("---- Scheduler: Iniciando la tarea de obtener personajes ----");
        try {
            // Paso 1: Obtenemos el número total de personajes si es la primera vez que se ejecuta.
            if (totalCharacters == 0) {
                RickAndMortyResponse response = rickAndMortyApi.getCharacters();
                if (response != null && response.info != null) {
                    totalCharacters = response.info.count;
                    LOG.info("Total de personajes disponibles: " + totalCharacters);
                } else {
                    // Si no se puede obtener el total, salimos del método para evitar errores.
                    LOG.error("No se pudo obtener el total de personajes. El scheduler no se ejecutará correctamente.");
                    return;
                }
            }

            // Si el número total es válido, continuamos con la tarea.
            if (totalCharacters > 0) {
                // Paso 2: Creamos una lista de 5 IDs aleatorios y únicos.
                Set<Integer> randomIds = new HashSet<>();
                Random random = new Random();
                while (randomIds.size() < 5) {
                    int randomId = random.nextInt(totalCharacters) + 1;
                    randomIds.add(randomId);
                }
                LOG.info("IDs aleatorios generados: " + randomIds);

                // Paso 3: Convertimos los IDs en una cadena de texto para la API.
                String ids = randomIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                // Paso 4: Llamamos a la API para obtener los personajes con esos IDs.
                List<RickAndMortyResponse.Character> characters = rickAndMortyApi.getCharactersByIds(ids);

                // Paso 5: Mostramos los resultados en la consola.
                LOG.info("Se obtuvieron los siguientes 5 personajes:");
                if (characters != null && !characters.isEmpty()) {
                    // Usamos los getters generados por Lombok.
                    characters.forEach(character ->
                            LOG.info(" -> Nombre: " + character.getName() +
                                    ", Especie: " + character.getSpecies() +
                                    ", Estado: " + character.getStatus())
                    );
                } else {
                    LOG.warn("La respuesta de la API no contiene personajes para los IDs generados.");
                }
            }
        } catch (Exception e) {
            // Si algo sale mal, mostramos un mensaje de error.
            LOG.error("Ocurrió un error al obtener personajes de la API: " + e.getMessage(), e);
        }
        LOG.info("---- Scheduler: Tarea finalizada ----");
    }
}