package com.classagendag2.features.user.data.local.dao;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.user.data.local.entity.UserEntity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserDao {
    private final DbConnectionFactory connectionFactory;

    // Recibimos la fábrica de conexiones que configuramos en sprints pasados
    public UserDao(DbConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public UserEntity insert(UserEntity entity) {
        String query = "INSERT INTO USERS (name, email, created_at) VALUES (?, ?, ?)";

        // El 'try-with-resources' (los parentesis tras el try) es vital: cierra la conexión
        //de red automáticamente al terminar, evitando que el servidor colapse
        try (Connection connection = connectionFactory.open();
            //RETURN_GENERATED_KEYS es la petición especial a SQL Server para que nos
             // confiese qué ID numérico (IDENTITY) le ha asignado a este usuario nuevo
             PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            //Sustituimos las interrogaciones por los datos reales de forma segura
            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getEmail());
            pstmt.setObject(3, entity.getCreatedAt());

            pstmt.executeUpdate(); // Ordenamos la inserción real en disco

            // rescatamos el ID autogenerado que nos devuelve la base de datos
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) { //Movemos el cursor a la primera fila devuelta
                    entity.setId(generatedKeys.getLong(1)); // Extraemos el ID y actualizamos la Entity
                    return entity;
                } else {
                    throw new SQLException("Fallo critico: No se obtuvo el ID autogenerado");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al insertar usuario", e);
        }
    }

    public Optional<UserEntity> findByEmail(String email) {
        String query = "SELECT id, name, email, created_at FROM USERS WHERE email = ?";

        try (Connection connection = connectionFactory.open();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    // si encuentra una fila, usamos un metodo privado de apoyo para mapearla
                    return Optional.of(mapResultSetToEntity(resultSet));
                }
                return Optional.empty(); // si no hay filas, devolvemos la caja vacia
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar usuario por email", e);
        }
    }

    public Optional<UserEntity> findById(Long id) {
        String query = "SELECT id, name, email, created_at FROM USERS WHERE id = ?";

        try (Connection connection = connectionFactory.open();
        PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setLong(1, id);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToEntity(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar usuario por ID", e);
        }
    }

    public void update(UserEntity entity) {
        String query = "UPDATE USERS SET name = ?, email = ? WHERE id = ?";

        try (Connection connection = connectionFactory.open();
            PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getEmail());
            pstmt.setLong(3, entity.getId()); // La condicion WHERE

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update fallido: el usuario con ese ID no existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar usuario", e);
        }
    }

    public List<UserEntity> findAll() {
        String query = "SELECT id, name, email, created_at FROM USERS";
        List<UserEntity> list = new ArrayList<>();

        try (Connection connection = connectionFactory.open();
        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet resultSet = pstmt.executeQuery()) {

            // Bucle que recorre todas las filas encontradas
            while (resultSet.next()) {
                list.add(mapResultSetToEntity(resultSet));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al recuperar todos los usuarios", e);
        }
    }

    public void deleteById(Long id) {
        String query = "DELETE FROM USERS WHERE id = ?";

        try (Connection connection = connectionFactory.open();
        PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate(); // ejecutamos la destrucción de la fila
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al borrar usuario", e);
        }
    }

    // metodo privado centralizado para no reepetir la lectura de columnas una y otra vez
    private UserEntity mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return new UserEntity(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getObject("created_at", LocalDateTime.class)
        );
    }
}