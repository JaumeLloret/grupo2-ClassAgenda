package com.classagendag2.features.event.domain.repository;

import com.classagendag2.features.event.domain.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    /**
     * Guarda un nuevo evento en la base de datos.
     *
     * @return
     */
    Event save(Event event);

    /**
     * Devuelve todos los eventos cuyo owner_id coincide con el usuario.
     */
    List<Event> findAllByOwner(Long ownerId);

    /**
     * Busca un evento por su ID.
     */
    Optional<Event> findById(Long id);

    /**
     * Actualiza un evento existente.
     */
    void update(Event event);

    /**
     * Elimina un evento por ID.
     */
    void deleteById(Long id);

    List<Event> findAll();
}
