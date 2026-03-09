package com.classagendag2.features.user.data.local.dao;

import com.classagendag2.features.user.data.local.entity.UserEntity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserDao {
    // 1. INYECCIÓN DE DEPENDENCIAS: Dependemos exclusivamente de la abstracción nativa de Java
    private final Connection connection;

    // Exigimos que la conexión ya abierta nos sea inyectada
    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public UserEntity insert(UserEntity entity) {
        String query = "INSERT INTO USERS (name, email, created_at) VALUES (?, ?, ?)";

        // 2. FÍJATE BIEN: Solo envolvemos el PreparedStatement. La connection queda fuera del try-with-resources.
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getEmail());
            pstmt.setObject(3, entity.getCreatedAt());

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
    public void update(UserEntity entity) {
        String query = "UPDATE USERS SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getEmail());
            pstmt.setLong(3, entity.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Update fallido: El usuario no existe.");
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando usuario", e);
        }
    }

    public Optional<UserEntity> findByEmail(String email) {
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
    }

    public Optional<UserEntity> findById(Long id) {
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
    }

    public List<UserEntity> findAll() {
        String query = "SELECT id, name, email, created_at FROM USERS";
        List<UserEntity> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) list.add(mapResultSetToEntity(resultSet));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error recuperando todos los usuarios", e);
        }
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM USERS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error borrando usuario", e);
        }
    }

    private UserEntity mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new UserEntity(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getObject("created_at", LocalDateTime.class)
        );
    }
}