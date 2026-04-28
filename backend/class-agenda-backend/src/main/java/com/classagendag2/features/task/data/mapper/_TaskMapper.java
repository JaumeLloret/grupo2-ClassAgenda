package com.classagendag2.features.task.data.mapper;

import com.classagendag2.features.task.data.local.entity._TaskEntity;
import com.classagendag2.features.task.domain.model._Task;
import com.classagendag2.features.task.domain.model.TaskPriority;
import com.classagendag2.features.task.domain.model.TaskStatus;

public final class _TaskMapper {
    // Constructor privado para impedir que cualquier programador intente hacer un 'new TaskMapper()'
    private _TaskMapper() {}

    public static _TaskEntity toEntity(_Task task) {
        if (task == null) return null;
        return new _TaskEntity(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(), // .name() extrae la palabra exacta del Enum (ej. "PENDING")
                task.getPriority().name(),
                task.getOwnerId(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }

    public static _Task toDomain(_TaskEntity entity) {
        if (entity == null) return null;

        // Al crear el objeto Task, los datos vuelven a pasar por la aduana de validación de forma segura
        return new _Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                TaskStatus.valueOf(entity.getStatus()), // .valueOf() convierte el texto de SQL en un Enum verificando que sea legal
                TaskPriority.valueOf(entity.getPriority()),
                entity.getOwnerId(),
                entity.getDueDate(),
                entity.getCreatedAt()
        );
    }
}