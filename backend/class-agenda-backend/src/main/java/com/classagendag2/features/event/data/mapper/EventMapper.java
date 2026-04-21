package com.classagendag2.features.event.data.mapper;

import com.classagendag2.features.event.domain.model.Event;
import com.classagendag2.features.event.data.local.entity.EventEntity;

public final class EventMapper {
    // Hacemos el constructor privado para bloquear a cualquiera que intente instanciarlo con 'new'
    private EventMapper() {
        // Evita instanciación
    }

    // TRADUCTOR A JAPONÉS: Convierte el Dominio a Entity para poder guardarlo en BD
    public static EventEntity toEntity(Event event) {
        if (event == null) return null;

        return new EventEntity(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStatus(),
                event.getPriority(),
                event.getLocation(),
                event.getStartAt(),
                event.getEndAt(),
                event.getOwner_id(),
                event.getCreatedAt()
        );
    }

    // TRADUCTOR A ESPAÑOL: Convierte la Entity a Dominio para devolvérselo al programa seguro
    public static Event toDomain(EventEntity entity) {
        if (entity == null) return null;

        return new Event(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getLocation(),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getOwner_id(),
                entity.getCreatedAt()
        );
    }
}
