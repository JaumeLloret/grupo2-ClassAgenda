package com.classagendag2.features.user.domain.usecase;

import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;

import java.util.Optional;

public final class GetUserByIdUseCase {

    private final UserRepository repository;

    public GetUserByIdUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> execute(Long id) {
        return repository.findById(id);
    }
}
