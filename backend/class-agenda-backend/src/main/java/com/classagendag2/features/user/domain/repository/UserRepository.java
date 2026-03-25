package com.classagendag2.features.user.domain.repository;

import com.classagendag2.features.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // Si el usuario es nuevo lo guarda. Si ya existe, lo actualiza.
    User save(User user);

    // Devuelve una caja que puede contener al usuario buscado por correo
    Optional<User> findByEmail(String email);

    // Devuelve una caja que puede contener al usuario buscado por ID numérico
    Optional<User> findById(Long id);

    // Devuelve una lista con todos los usuarios (si no hay ninguno, devolverá una lista vacía)
    List<User> findAll();

    // Elimina al usuario indicado
    void deleteById(Long id);
}
