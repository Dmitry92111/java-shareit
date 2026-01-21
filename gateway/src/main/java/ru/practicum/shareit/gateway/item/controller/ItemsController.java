package ru.practicum.shareit.gateway.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.client.ItemsClient;
import ru.practicum.shareit.gateway.item.dto.CommentCreateRequest;
import ru.practicum.shareit.gateway.item.dto.ItemCreateDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;
import ru.practicum.shareit.gateway.util.Headers;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemsController {

    private final ItemsClient itemsClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Headers.USER_ID) Long userId,
                                         @Valid @RequestBody ItemCreateDto body) {
        return itemsClient.create(userId, body);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(Headers.USER_ID) Long userId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody ItemUpdateDto body) {
        return itemsClient.update(userId, itemId, body);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(Headers.USER_ID) Long userId) {
        return itemsClient.getOwnerItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(Headers.USER_ID) Long userId,
                                          @PathVariable long itemId) {
        return itemsClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemsClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Headers.USER_ID) Long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentCreateRequest body) {
        return itemsClient.addComment(userId, itemId, body);
    }
}
