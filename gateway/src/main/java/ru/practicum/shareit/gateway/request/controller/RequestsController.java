package ru.practicum.shareit.gateway.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.request.client.RequestsClient;
import ru.practicum.shareit.gateway.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.gateway.util.Headers;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestsController {

    private final RequestsClient requestsClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Headers.USER_ID) Long userId,
                                         @Valid @RequestBody ItemRequestCreateRequest body) {
        return requestsClient.create(userId, body);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(Headers.USER_ID) Long userId) {
        return requestsClient.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(Headers.USER_ID) Long userId,
                                         @RequestParam(required = false) Integer from,
                                         @RequestParam(required = false) Integer size) {
        return requestsClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(Headers.USER_ID) Long userId,
                                          @PathVariable long requestId) {
        return requestsClient.getById(userId, requestId);
    }
}
