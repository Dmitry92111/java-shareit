package ru.practicum.shareit.gateway.user.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class UsersClient extends BaseClient {

    public UsersClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Object body) {
        return post("/users", null, body);
    }

    public ResponseEntity<Object> getById(long id) {
        return get("/users/{id}", null, Map.of("id", id));
    }

    public ResponseEntity<Object> getAll() {
        return get("/users", null);
    }

    public ResponseEntity<Object> update(long id, Object body) {
        return patch("/users/{id}", null, Map.of("id", id), body);
    }

    public ResponseEntity<Object> deleteById(long id) {
        return delete("/users/{id}", null, Map.of("id", id));
    }
}
