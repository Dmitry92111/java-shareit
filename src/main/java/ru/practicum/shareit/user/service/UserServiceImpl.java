package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.type.DuplicatedDataException;
import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.ServiceUtils;

import java.util.Collection;

import static ru.practicum.shareit.exception.ExceptionMessages.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User findById(Long id) {
        log.debug("Trying to find user by id {}", id);
        ServiceUtils.checkNotNull(id, "id");
        return userStorage.findById(id)
                .map(user -> {
                    log.info("User with id {} has been found successfully", id);
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("User with id {} not found", id);
                    return new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", id));
                });
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Trying to find all users");
        return userStorage.findAll();
    }

    @Override
    @Transactional
    public User create(User user) {
        log.debug("Trying to create user {}", user);
        if (userStorage.existsByEmail(user.getEmail())) {
            log.warn("User with email {} already exists", user.getEmail());
            throw new DuplicatedDataException(String.format(EMAIL_ALREADY_EXIST, user.getEmail()));
        }
        user = userStorage.save(user);
        log.info("User has been created successfully with id {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        log.debug("Trying to update user {}", user);
        User existingUser = findById(id);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            if (!user.getEmail().equals(existingUser.getEmail()) && userStorage.existsByEmailAndIdNot(user.getEmail(), id)) {
                log.warn("Cannot update User with id = {}, email: {} already exists in storage",
                        user.getId(), user.getEmail());
                throw new DuplicatedDataException(String.format(EMAIL_ALREADY_EXIST, user.getEmail()));
            }
            existingUser.setEmail(user.getEmail());
        }

        User updatedUser = userStorage.update(existingUser);
        log.info("User with id {} has been updated successfully", id);
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Trying to delete user with id {}", id);
        findById(id);
        userStorage.deleteById(id);
        log.info("User with id {} has been deleted successfully", id);
    }
}
