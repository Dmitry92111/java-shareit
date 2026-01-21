package ru.practicum.shareit.gateway.request.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;

@Service
public class RequestsClient extends BaseClient {

    public RequestsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("/requests", userId, body);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        return get("/requests/all?from={from}&size={size}",
                userId,
                Map.of(
                        "from", from == null ? 0 : from,
                        "size", size == null ? 20 : size
                )
        );
    }

    public ResponseEntity<Object> getById(Long userId, long requestId) {
        return get("/requests/{requestId}", userId, Map.of("requestId", requestId));
    }
}
