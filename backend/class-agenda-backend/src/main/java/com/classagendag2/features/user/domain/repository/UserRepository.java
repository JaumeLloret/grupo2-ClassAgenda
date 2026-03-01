package com.classagendag2.features.user.domain.repository;

import com.classagendag2.features.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    User update(User user);

    boolean delete(Long id);
}
