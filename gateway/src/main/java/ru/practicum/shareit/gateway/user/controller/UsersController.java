package ru.practicum.shareit.gateway.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.user.client.UsersClient;
import ru.practicum.shareit.gateway.user.dto.UserCreateRequest;
import ru.practicum.shareit.gateway.user.dto.UserUpdateRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersClient usersClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateRequest body) {
        return usersClient.create(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id) {
        return usersClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return usersClient.getAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @Valid @RequestBody UserUpdateRequest body) {
        return usersClient.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long id) {
        return usersClient.deleteById(id);
    }
}
