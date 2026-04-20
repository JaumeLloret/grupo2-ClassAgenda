package com.classagendag2.features.event.data.repository;

import com.classagendag2.features.event.data.local.dao.EventDao;
import com.classagendag2.features.event.data.local.entity.EventEntity;
import com.classagendag2.features.event.data.mapper.EventMapper;
import com.classagendag2.features.event.domain.model.Event;
import com.classagendag2.features.event.domain.repository.EventRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JdbcEventRepository implements EventRepository {
    private final EventDao eventDao;

    // Inyectamos el DAO en el constructor
    public JdbcEventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public Event save(Event userToSave) {
        // 1. Traducción inicial
        EventEntity entityToSave = EventMapper.toEntity(userToSave);

        // 2. Logica de enrutamiento
        if (entityToSave.getId() == null) {
            // Es el usuario nuevo; Lo insertamos y traducimos la respuesta ( que ya trae el ID
            EventEntity insertedEntity = eventDao.insert(entityToSave);
            return EventMapper.toDomain(insertedEntity);
        } else {
            // Ya existe; lo actualizamos y devolvemos el usuario intacto
            eventDao.update(entityToSave);
            return userToSave;
        }
    }

    @Override
    public Optional<Event> findByEmail(String email) {
        // En lugar de hacer if/else pesados, la caja Optional tiene un metodo '.map'.
        // Esto le dice a Java: "Si la caja trae una Entity, aplicale el traductor
        // UserMapper:: toDamain dentro de la propia caja y devuelvemela trnsformada"
        return eventDao.findByEmail(email).map(EventMapper::toDomain);

    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventDao.findById(id).map(EventMapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        List<EventEntity> entityList = eventDao.findAll();

        // Aplicamos la magia funcional de los Streams
        return entityList.stream()
                .map(EventMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        // El borrado no necesita traduccion, simplemente enviamos la orden al DAO
        eventDao.deleteById(id);
    }
}
