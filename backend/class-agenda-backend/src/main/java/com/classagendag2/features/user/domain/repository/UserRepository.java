package com.classagendag2.features.user.domain.repository;

import com.classagendag2.features.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);
}
