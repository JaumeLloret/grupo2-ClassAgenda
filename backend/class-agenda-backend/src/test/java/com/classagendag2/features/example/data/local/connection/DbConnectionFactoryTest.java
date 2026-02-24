package com.classagendag2.features.example.data.local.connection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DbConnectionFactoryTest {
    @Test
    void factory_canBeCreated() {
        // Comprobamos que podemos construir la fábrica de forma segura
        DbConnectionFactory factory = new DbConnectionFactory();
        assertNotNull(factory);
    }
}