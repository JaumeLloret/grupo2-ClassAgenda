package com.classagendag2.features.task.domain.repository;

import com.classagendag2.features.task.domain.model.Task;
import com.classagendag2.features.task.domain.model.TaskStatus;
import java.util.List;
import java.util.Optional;

// IMPORTANTE: En la convención moderna de Java, las interfaces no llevan una 'I' delante.
// Se llaman por su nombre puro (TaskRepository) y la implementación llevará el detalle técnico.
public interface TaskRepository {
    void update(Task task);

    Task save(Task task);

    // La clase Optional actúa como una "caja de seguridad". Evita que el programa explote
    // con un NullPointerException si intentamos buscar un ID que ya no existe.
    Optional<Task> findById(Long id);

    void deleteById(Long id);

    // --- FILTROS DE NEGOCIO ---
    List<Task> findByOwnerId(Long ownerId);
    List<Task> findByOwnerIdAndStatus(Long ownerId, TaskStatus status);

    boolean hasSharePermission(Long taskId, Long requestingUserId);
}