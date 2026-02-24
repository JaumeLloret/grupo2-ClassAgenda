package com.classagendag2.features.example.data.local.connection;

import java.sql.Connection;

public final class DbSmokeTest {
    public static void main(String[] args) throws Exception {
        DbConnectionFactory connectionFactory = new DbConnectionFactory();

        // El try-with-resources abrirá y luego CERRARÁ la conexión automáticamente
        try (Connection connection = connectionFactory.open()) {
            // Obtenemos el nombre del motor para demostrar que ha conectado
            String databaseName = connection.getMetaData().getDatabaseProductName();
            System.out.println("✅ DB connected: " + databaseName);
        }
    }
}