package ru.practicum.shareit.gateway.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path, @Nullable Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path,
                                         @Nullable Long userId,
                                         @Nullable Map<String, Object> params) {
        return exchange(path, HttpMethod.GET, userId, params, null);
    }

    protected ResponseEntity<Object> post(String path, @Nullable Long userId, @Nullable Object body) {
        return post(path, userId, null, body);
    }

    protected ResponseEntity<Object> post(String path,
                                          @Nullable Long userId,
                                          @Nullable Map<String, Object> params,
                                          @Nullable Object body) {
        return exchange(path, HttpMethod.POST, userId, params, body);
    }

    protected ResponseEntity<Object> patch(String path, @Nullable Long userId, @Nullable Object body) {
        return patch(path, userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path,
                                           @Nullable Long userId,
                                           @Nullable Map<String, Object> params,
                                           @Nullable Object body) {
        return exchange(path, HttpMethod.PATCH, userId, params, body);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path,
                                            @Nullable Long userId,
                                            @Nullable Map<String, Object> params) {
        return exchange(path, HttpMethod.DELETE, userId, params, null);
    }

    private ResponseEntity<Object> exchange(String path,
                                            HttpMethod method,
                                            @Nullable Long userId,
                                            @Nullable Map<String, Object> params,
                                            @Nullable Object body) {

        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            if (params == null) {
                return rest.exchange(path, method, requestEntity, Object.class);
            }
            return rest.exchange(path, method, requestEntity, Object.class, params);
        } catch (HttpStatusCodeException e) {
            // возвращаем ответ server как есть, чтобы gateway не превращал его в 500
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsByteArray());
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Server is unavailable: " + e.getMessage()));
        }
    }
}
