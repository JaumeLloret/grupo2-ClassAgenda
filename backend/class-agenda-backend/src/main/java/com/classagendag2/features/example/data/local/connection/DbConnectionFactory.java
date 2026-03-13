package com.classagendag2.features.example.data.local.connection;

import com.classagendag2.shared.config.DbConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionFactory {
    public Connection open() throws SQLException {
        // Pedimos al DriverManager que establezca la conexión real
        return DriverManager.getConnection(
                DbConfig.url(),
                DbConfig.user(),
                DbConfig.password()
        );
    }
}