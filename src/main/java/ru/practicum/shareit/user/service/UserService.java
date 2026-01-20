package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User findById(Long id);

    Collection<User> findAll();

    User create(User user);

    User update(Long id, User user);

    void deleteById(Long id);
}
