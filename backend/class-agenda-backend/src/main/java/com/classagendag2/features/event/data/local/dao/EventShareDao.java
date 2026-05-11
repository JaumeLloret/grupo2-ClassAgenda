package com.classagendag2.features.event.data.local.dao;

import com.classagendag2.features.event.domain.model.PermissionLevel;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public final class EventShareDao {

    private final Connection connection;

    public EventShareDao(Connection connection) {
        this.connection = connection;
    }

    public void upsertShare(Long eventId, Long sharedWithUserId, PermissionLevel level) {
        String sql = """
            MERGE INTO EVENT_SHARES AS target
            USING (SELECT ? AS event_id, ? AS shared_with_user_id) AS source
            ON target.event_id = source.event_id
            AND target.shared_with_user_id = source.shared_with_user_id
            WHEN MATCHED THEN
                UPDATE SET permission = ?, shared_at = ?
            WHEN NOT MATCHED THEN
                INSERT (event_id, shared_with_user_id, permission, shared_at)
                VALUES (?, ?, ?, ?);
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            Timestamp now = Timestamp.valueOf(LocalDateTime.now());

            // MATCH
            stmt.setLong(1, eventId);
            stmt.setLong(2, sharedWithUserId);

            // UPDATE
            stmt.setString(3, level.name());
            stmt.setTimestamp(4, now);

            // INSERT
            stmt.setLong(5, eventId);
            stmt.setLong(6, sharedWithUserId);
            stmt.setString(7, level.name());
            stmt.setTimestamp(8, now);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la compartición en BD", e);
        }
    }

    public void deleteShare(Long eventId, Long sharedWithUserId) {
        String sql = "DELETE FROM EVENT_SHARES WHERE event_id = ? AND shared_with_user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setLong(2, sharedWithUserId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al revocar la compartición", e);
        }
    }

    public Optional<PermissionLevel> getPermissionLevel(Long eventId, Long sharedWithUserId) {
        String sql = "SELECT permission FROM EVENT_SHARES WHERE event_id = ? AND shared_with_user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setLong(2, sharedWithUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(PermissionLevel.valueOf(rs.getString("permission")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando permisos", e);
        }
    }
}
