package com.classagendag2.features.event.domain.repository;

import com.classagendag2.features.event.domain.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {

    // Si el usuario es nuevo lo guarda. Si ya existe, lo actualiza.
    Event save(Event event);

    // Devuelve una caja que puede contener al usuario buscado por correo
    Optional<Event> findByEmail(String email);

    // Devuelve una caja que puede contener al usuario buscado por ID numérico
    Optional<Event> findById(Long id);

    // Devuelve una lista con todos los usuarios (si no hay ninguno, devolverá una lista vacía)
    List<Event> findAll();

    // Elimina al usuario indicado
    void deleteById(Long id);
}
