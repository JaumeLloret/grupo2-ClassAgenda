package com.classagendag2.features.user.data.mapper;

import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.data.local.entity.UserEntity;

public final class UserMapper {
    private UserMapper() {
        // Evita instanciación
    }

    public static  UserEntity toEntity(User user) {
        if (user == null) return null;

        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null  //createdAt lo gestiona la BD
        );
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }
}
