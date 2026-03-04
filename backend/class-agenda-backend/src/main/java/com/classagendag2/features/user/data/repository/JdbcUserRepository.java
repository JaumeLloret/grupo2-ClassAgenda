package com.classagendag2.features.user.data.repository;

import com.classagendag2.features.user.data.local.dao.UserDao;
import com.classagendag2.features.user.data.local.entity.UserEntity;
import com.classagendag2.features.user.data.mapper.UserMapper;
import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class JdbcUserRepository implements UserRepository {
    private final UserDao userDao;

    // Inyectamos el DAO en el constructor
    public JdbcUserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User save(User userToSave) {
        // 1. Traducción inicial
        UserEntity entityToSave = UserMapper.toEntity(userToSave);

        // 2. Logica de enrutamiento
        if (entityToSave.getId() == null) {
            // Es el usuario nuevo; Lo insertamos y traducimos la respuesta ( que ya trae el ID
            UserEntity insertedEntity = userDao.insert(entityToSave);
            return UserMapper.toDomain(insertedEntity);
        } else {
            // Ya existe; lo actualizamos y devolvemos el usuario intacto
            userDao.update(entityToSave);
            return userToSave;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // En lugar de hacer if/else pesados, la caja Optional tiene un metodo '.map'.
        // Esto le dice a Java: "Si la caja trae una Entity, aplicale el traductor
        // UserMapper:: toDamain dentro de la propia caja y devuelvemela trnsformada"
        return userDao.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userDao.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> entityList = userDao.findAll();

        // Aplicamos la magia funcional de los Streams
        return entityList.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        // El borrado no necesita traduccion, simplemente enviamos la orden al DAO
        userDao.deleteById(id);
    }
}
