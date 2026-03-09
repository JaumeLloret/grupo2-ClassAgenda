package com.classagendag2.features.user.data.repository;

import com.classagendag2.features.example.data.local.connection.DbConnectionFactory;
import com.classagendag2.features.user.data.local.dao.UserDao;
import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.shared.config.DbConfig;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import  static org.junit.jupiter.api.Assertions.*;

class JdbcUserRepositoryIT {
    private JdbcUserRepository userRepository;

    @BeforeEach
    void setUp() {
        // EL ESCUDO PROTECTOR DE CI/CD
        try {
            DbConfig.url(); // Intentamos forzar la lectura del .env
        } catch (Exception e) {
            // Si salta un error (por ejemplo, porque estamos en la máquina virtual de Github),
            // cancelamos el test pacificamente para que no rompa la subida del codigo
            Assumptions.abort("Saltando test de integración: no hay BD configurada (.env).");
        }

        // si el codigo sobrevive y llega hasta aquí, significa que SI hay un .env local valido
        // inicializamos la fabrica, el DAO y finalmente el repositorio
        userRepository = new JdbcUserRepository(new UserDao((Connection) new DbConnectionFactory()));
    }

    @Test
    void savesNewUserAndFindsItByEmail() {
        // Generamos un correo unico basado en la hora actual para que el test no choque con
        // ejecuciones anteriores (ya que el email en la BD tiene la restriccion UNIQUE)
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@ejemplo.com";
        User userToSave = new User("Prueba Integracion", uniqueEmail);

        // ACTUAMOS: Ejecutamos el guardado en la base de datos
        User savedUser = userRepository.save(userToSave);

        // COMPROBACIONES (ASSERT): Verificamos que el usuario vuelve con un ID real
        assertNotNull(savedUser.getId(), "El usuario debería tener un ID de base de datos");
        assertEquals("Prueba Integracion", savedUser.getName());
        assertEquals(uniqueEmail, savedUser.getEmail());

        // SEGUNDO ACTO: Volvemos a pedirle a la base de datos que busque ese correo
        Optional<User> foundUserOpt = userRepository.findByEmail(uniqueEmail);
        assertTrue(foundUserOpt.isPresent(), "Deberiamos poder encontrar el usuario");

        User foundUser = foundUserOpt.get(); // Extraemos el usuario de la caja de seguridad
        assertEquals(savedUser.getId(), foundUser.getId());

        // IMPORTANTE: Comprobamos las fechas truncándolas a segundos para evitar los falsos
        // errores producidos por la diferencia de precision entre Java y SQL Server
        assertEquals(
                savedUser.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
                foundUser.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
