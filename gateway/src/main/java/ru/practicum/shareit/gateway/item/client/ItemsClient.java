package ru.practicum.shareit.gateway.item.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class ItemsClient extends BaseClient {

    public ItemsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("/items", userId, body);
    }

    public ResponseEntity<Object> update(Long userId, long itemId, Object body) {
        return patch("/items/{itemId}", userId, Map.of("itemId", itemId), body);
    }

    public ResponseEntity<Object> getOwnerItems(Long userId) {
        return get("/items", userId);
    }

    public ResponseEntity<Object> getById(Long userId, long itemId) {
        return get("/items/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> search(String text) {
        return get("/items/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, long itemId, Object body) {
        return post("/items/{itemId}/comment", userId, Map.of("itemId", itemId), body);
    }
}
