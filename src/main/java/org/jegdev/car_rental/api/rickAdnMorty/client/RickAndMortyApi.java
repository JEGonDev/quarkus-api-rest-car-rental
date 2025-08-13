package org.jegdev.car_rental.api.rickAdnMorty.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jegdev.car_rental.api.rickAdnMorty.dto.RickAndMortyResponse;

import java.util.List;

/**
 * Interfaz de cliente REST para la API de Rick and Morty.
 * La anotación @RegisterRestClient es CRUCIAL para que Quarkus sepa cómo inyectarla.
 */
@Path("/character")
@RegisterRestClient(configKey = "rickandmortyapi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RickAndMortyApi {

    /**
     * Obtiene la primera página de personajes para obtener el conteo total.
     * Retorna el objeto de respuesta completo.
     */
    @GET
    RickAndMortyResponse getCharacters();

    /**
     * Obtiene múltiples personajes por sus IDs.
     * Cuando solicitas múltiples IDs, la API devuelve un array directamente,
     * no un objeto RickAndMortyResponse.
     *
     * @param ids Una cadena con los IDs de los personajes separados por comas.
     * @return Lista de personajes directamente.
     */
    @GET
    @Path("/{ids}")
    List<RickAndMortyResponse.Character> getCharactersByIds(@PathParam("ids") String ids);
}