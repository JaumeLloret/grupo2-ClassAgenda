package com.classagendag2.features.user.data.local.dao;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.user.data.local.entity.UserEntity;
import com.classagendag2.features.user.data.mapper.UserMapper;
import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UserRepositoryImpl implements UserRepository {

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = new DbConnectionFactory().open();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                Long id = keys.getLong(1);
                return new User(id, user.getName(), user.getEmail(), null);
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql ="SELECT id, name, email, created_at FROM users WHERE id = ?";

        try (Connection conn = new DbConnectionFactory().open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs =stmt.executeQuery();

            if (rs.next()) {
                UserEntity entity = new UserEntity(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                return Optional.of(UserMapper.toDomain(entity));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email, created_at FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = new DbConnectionFactory().open();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UserEntity entity = new UserEntity(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );

                users.add(UserMapper.toDomain(entity));
            }

            return users;
        } catch (SQLException e) {
            throw  new RuntimeException("Error listing users", e);
        }
    }

    @Override
    public  User update(User user) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = new DbConnectionFactory().open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setLong(3, user.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return user;
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error update user", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql ="DELETE FROM USERS WHERE id = ?";

        try (Connection conn = new DbConnectionFactory().open();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
}
