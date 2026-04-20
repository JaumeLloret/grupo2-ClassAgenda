package com.classagendag2.features.event.data.local.dao;

import com.classagendag2.features.event.data.local.entity.EventEntity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EventDao {
    // 1. INYECCIÓN DE DEPENDENCIAS: Dependemos exclusivamente de la abstracción nativa de Java
    private final Connection connection;

    // Exigimos que la conexión ya abierta nos sea inyectada
    public EventDao(Connection connection) {
        this.connection = connection;
    }

    public EventEntity insert(EventEntity entity) {
        String query = "INSERT INTO EVENTS (title, description, status, priority, owner_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        // 2. FÍJATE BIEN: Solo envolvemos el PreparedStatement. La connection queda fuera del try-with-resources.
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getDescription());
            pstmt.setString(3, entity.getStatus());
            pstmt.setString(4, entity.getPriority());
            pstmt.setInt(5, entity.getOwner_id());
            pstmt.setObject(6, entity.getCreatedAt());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return entity;
                } else {
                    throw new SQLException("Error crítico: No se recuperó el ID.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando usuario", e);
        }
    }
    public void update(EventEntity entity) {
        String query = "UPDATE EVENTS SET title = ?, description = ?, status = ?, priority = ?, owner_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getDescription());
            pstmt.setString(3, entity.getStatus());
            pstmt.setString(4, entity.getPriority());
            pstmt.setInt(5, entity.getOwner_id());
            pstmt.setObject(6, entity.getCreatedAt());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Update fallido: El evento no existe.");
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando evento", e);
        }
    }

    /*public Optional<EventEntity> findByEmail(String email) {
        String query = "SELECT id, name, email, created_at FROM USERS WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapResultSetToEntity(resultSet));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario por email", e);
        }
    }*/

    /*public Optional<EventEntity> findById(Long id) {
        String query = "SELECT id, name, email, created_at FROM USERS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapResultSetToEntity(resultSet));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario por ID", e);
        }
    }*/

    public List<EventEntity> findAll() {
        String query = "SELECT id, title, description, status, priority, owner_id, created_at FROM EVENTS";
        List<EventEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) list.add(mapResultSetToEntity(resultSet));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error recuperando todos los eventos", e);
        }
    }

    /*public void deleteById(Long id) {
        String query = "DELETE FROM USERS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error borrando usuario", e);
        }
    }*/

    private EventEntity mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new EventEntity(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("status"),
                resultSet.getString("priority"),
                resultSet.getInt("owner_id"),
                resultSet.getObject("created_at", LocalDateTime.class)
        );
    }
}