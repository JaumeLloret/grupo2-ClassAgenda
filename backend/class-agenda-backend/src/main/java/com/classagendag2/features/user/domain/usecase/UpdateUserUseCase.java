package com.classagendag2.features.user.domain.usecase;

import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;

public final class UpdateUserUseCase {
    private final UserRepository repository;

    public UpdateUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public User execute(User user) {
        return repository.update(user);
    }
}
