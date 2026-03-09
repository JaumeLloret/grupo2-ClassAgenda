package com.classagendag2.features.task.data.repository;

import com.classagendag2.features.task.data.local.dao.TaskDao;
import com.classagendag2.features.task.data.local.entity.TaskEntity;
import com.classagendag2.features.task.data.mapper.TaskMapper;
import com.classagendag2.features.task.domain.model.Task;
import com.classagendag2.features.task.domain.model.TaskStatus;
import com.classagendag2.features.task.domain.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JdbcTaskRepository implements TaskRepository {
    private final TaskDao taskDao;

    // Aquí también aplicamos DI: el Repositorio exige recibir un DAO ya instanciado y listo
    public JdbcTaskRepository(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entityToSave = TaskMapper.toEntity(task);

        // Enrutamiento automático inteligente: Si no trae ID es creación; Si trae ID es actualización.
        if (entityToSave.getId() == null) {
            TaskEntity insertedEntity = taskDao.insert(entityToSave);
            return TaskMapper.toDomain(insertedEntity);
        } else {
            taskDao.update(entityToSave);
            return task;
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskDao.findById(id).map(TaskMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        taskDao.deleteById(id);
    }

    @Override
    public List<Task> findByOwnerId(Long ownerId) {
        // Implementación de cinta transportadora funcional (Streams)
        return taskDao.findByOwnerId(ownerId).stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByOwnerIdAndStatus(Long ownerId, TaskStatus status) {
        // Extraemos el nombre literal del Enum con .name() para podérselo pasar como texto al DAO
        return taskDao.findByOwnerIdAndStatus(ownerId, status.name()).stream()
                .map(TaskMapper::toDomain)
                .collect(Collectors.toList());
    }
}