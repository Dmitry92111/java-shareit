package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.type.DuplicatedDataException;
import ru.practicum.shareit.test.DbIntegrationTestBase;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.*;

class UserServiceImplDbTest extends DbIntegrationTestBase {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldPersistUser_andReturnId() {
        User u = User.builder().name("Ann").email("ann@mail.com").build();

        User created = userService.create(u);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Ann");
        assertThat(created.getEmail()).isEqualTo("ann@mail.com");
    }

    @Test
    void create_shouldThrowDuplicatedDataException_whenEmailAlreadyExists() {
        userService.create(User.builder().name("A").email("dup@mail.com").build());

        assertThatThrownBy(() ->
                userService.create(User.builder().name("B").email("dup@mail.com").build())
        ).isInstanceOf(DuplicatedDataException.class);
    }
}