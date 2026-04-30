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
                event.getStart_at(),
                event.getEnd_at(),
                event.getOwner_id(),
                event.getCreated_at()
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
                entity.getStart_at(),
                entity.getEnd_at(),
                entity.getOwner_id(),
                entity.getCreated_at()
        );
    }
}
