package com.classagendag2.features.user.domain.usecase;

import com.classagendag2.features.user.domain.repository.UserRepository;

public final class DeleteUserUseCase {

    private final UserRepository repository;

    public DeleteUserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public boolean execute(Long id) {
        return repository.delete(id);
    }
}
