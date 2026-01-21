package ru.practicum.shareit.gateway.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.booking.client.BookingsClient;
import ru.practicum.shareit.gateway.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.gateway.util.Headers;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingsController {

    private final BookingsClient bookingsClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Headers.USER_ID) Long userId,
                                         @Valid @RequestBody BookingCreateRequest body) {
        return bookingsClient.create(userId, body);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(Headers.USER_ID) Long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingsClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(Headers.USER_ID) Long userId,
                                          @PathVariable long bookingId) {
        return bookingsClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(Headers.USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        return bookingsClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(Headers.USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return bookingsClient.getOwnerBookings(userId, state, from, size);
    }
}
