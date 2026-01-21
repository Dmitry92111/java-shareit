package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(Long id);

    Collection<User> findAll();

    User save(User user);

    User update(User user);

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long excludeId);
}
