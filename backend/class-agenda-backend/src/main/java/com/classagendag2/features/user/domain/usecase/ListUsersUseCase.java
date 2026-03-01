package com.classagendag2.features.user.domain.usecase;


import com.classagendag2.features.user.domain.model.User;
import com.classagendag2.features.user.domain.repository.UserRepository;

import java.util.List;

public final class ListUsersUseCase {

    private final UserRepository repository;

    public ListUsersUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> execute() {
        return repository.findAll();
    }

}
