package com.classagendag2.features.user.domain.usecase;

import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;

public final class CreateUserUseCase {

    private final UserRepository repository;

    public CreateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(User user) {
        return repository.create(user);
    }
}
