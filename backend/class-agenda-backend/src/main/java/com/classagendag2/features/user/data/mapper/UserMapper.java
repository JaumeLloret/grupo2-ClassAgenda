package com.classagendag2.features.user.data.mapper;

import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.data.local.entity.UserEntity;

public final class UserMapper {
    // Hacemos el constructor privado para bloquear a cualquiera que intente instanciarlo con 'new'
    private UserMapper() {
        // Evita instanciación
    }

    // TRADUCTOR A JAPONÉS: Convierte el Dominio a Entity para poder guardarlo en BD
    public static  UserEntity toEntity(User user) {
        if (user == null) return null;

        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    // TRADUCTOR A ESPAÑOL: Convierte la Entity a Dominio para devolvérselo al programa seguro
    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }
}
