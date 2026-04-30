package com.classagendag2.features.event.domain.repository;

import com.classagendag2.features.event.domain.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {

    Event save(Event event);

    List<Event> findAllByOwner(Long ownerId);

    Optional<Event> findById(Long id);

    List<Event> findAll();

    void deleteById(Long id);
}
