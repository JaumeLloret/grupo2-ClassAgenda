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

    public void upsertShare(Long eventId, Long userId, PermissionLevel level) {
        // Usamos la sintaxis MERGE o la equivalente en SQL Server para hacer un UPSERT seguro.
        // Si existe la pareja (event_id, user_id), actualiza el permiso. Si no existe, lo inserta.
        String query = """
            MERGE INTO EVENT_SHARES AS target
            USING (SELECT ? AS event_id, ? AS user_id) AS source
            ON target.event_id = source.event_id AND target.user_id = source.user_id
            WHEN MATCHED THEN
                UPDATE SET permission_level = ?
            WHEN NOT MATCHED THEN
                INSERT (event_id, user_id, permission_level, created_at)
                VALUES (source.event_id, source.user_id, ?, ?);
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, eventId);
            pstmt.setLong(2, userId);
            pstmt.setString(3, level.name());
            pstmt.setString(4, level.name());
            pstmt.setObject(5, LocalDateTime.now());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la compartición en BD", e);
        }
    }
    public void deleteShare(Long eventId, Long userId) {
        String query = "DELETE FROM EVENT_SHARES WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, eventId);
            pstmt.setLong(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al revocar la compartición", e);
        }
    }

    public Optional<PermissionLevel> getPermissionLevel(Long eventId, Long userId) {
        String query = "SELECT permission_level FROM EVENT_SHARES WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, eventId);
            pstmt.setLong(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(PermissionLevel.valueOf(rs.getString("permission_level")));
                }
                return Optional.empty(); // No está compartido con esta persona
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando permisos", e);
        }
    }
}