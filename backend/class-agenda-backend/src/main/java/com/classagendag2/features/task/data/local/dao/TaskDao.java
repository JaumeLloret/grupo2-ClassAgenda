package com.classagendag2.features.task.data.local.dao;

import com.classagendag2.features.task.data.local.entity.TaskEntity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TaskDao {
    private final Connection connection; // Dependencia inyectada externamente

    public TaskDao(Connection connection) {
        this.connection = connection;
    }

    public TaskEntity insert(TaskEntity entity) {
        String query = "INSERT INTO TASKS (title, description, status, priority, owner_user_id, due_date, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Para protegernos absolutamente de la Inyección SQL, usamos siempre PreparedStatement con interrogaciones (?)
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getDescription());
            pstmt.setString(3, entity.getStatus());
            pstmt.setString(4, entity.getPriority());
            pstmt.setLong(5, entity.getOwnerId());
            pstmt.setString(6, entity.getDueDate());
            pstmt.setObject(7, entity.getCreatedAt());


            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return entity;
                } else throw new SQLException("Fallo crítico al obtener el ID autogenerado de la BD.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico insertando la tarea", e);
        }
    }

    public void update(TaskEntity entity) {
        String query = "UPDATE TASKS SET title = ?, description = ?, status = ?, priority = ?, due_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getDescription());
            pstmt.setString(3, entity.getStatus());
            pstmt.setString(4, entity.getPriority());
            pstmt.setString(5, entity.getDueDate());
            pstmt.setLong(6, entity.getId());

            int rows = pstmt.executeUpdate();
            if (rows == 0) throw new SQLException("Update fallido: Tarea inexistente.");
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico actualizando la tarea", e);
        }
    }

    public Optional<TaskEntity> findById(Long id) {
        String query = "SELECT id, title, description, status, priority, owner_user_id, due_date, created_at FROM TASKS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapResultSetToEntity(resultSet));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico buscando la tarea", e);
        }
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM TASKS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico borrando la tarea", e);
        }
    }

    public List<TaskEntity> findByOwnerId(Long ownerId) {
        String query = "SELECT id, title, description, status, priority, owner_user_id, due_date, created_at FROM TASKS WHERE owner_user_id = ?";
        List<TaskEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, ownerId);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) list.add(mapResultSetToEntity(resultSet));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico buscando tareas del propietario", e);
        }
    }

    // FILTRO COMPUESTO MÚLTIPLE (Con la cláusula AND)
    public List<TaskEntity> findByOwnerIdAndStatus(Long ownerId, String status) {
        String query = "SELECT id, title, description, status, priority, owner_user_id, due_date, created_at " +
                "FROM TASKS WHERE owner_user_id = ? AND status = ?";
        List<TaskEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, ownerId);
            pstmt.setString(2, status);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) list.add(mapResultSetToEntity(resultSet));
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error técnico filtrando tareas compuestas", e);
        }
    }

    private TaskEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new TaskEntity(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("status").toUpperCase(),
                normalizePriority(rs.getString("priority")),
                rs.getLong("owner_user_id"),
                rs.getString("due_date"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }
    private String normalizePriority(String raw) {
        if (raw == null) return null;

        String upper = raw.toUpperCase();

        switch (upper) {
            case "LOW":
                return "LOW";
            case "HIGH":
                return "HIGH";
            case "MEDIUM":   // ← valor que viene de SQL Server
                return "MED"; // ← valor que tu enum espera
            case "MED":
                return "MED";
            default:
                throw new IllegalArgumentException("Valor de prioridad inválido en BD: " + raw);
        }
    }

}

