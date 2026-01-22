package ru.practicum.shareit.gateway.booking.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BaseClient;

import java.util.Map;
import org.springframework.http.ResponseEntity;

@Service
public class BookingsClient extends BaseClient {

    public BookingsClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("/bookings", userId, body);
    }

    public ResponseEntity<Object> approve(Long ownerId, long bookingId, boolean approved) {
        return patch("/bookings/{bookingId}?approved={approved}",
                ownerId,
                Map.of("bookingId", bookingId, "approved", approved),
                null
        );
    }

    public ResponseEntity<Object> getById(Long userId, long bookingId) {
        return get("/bookings/{bookingId}", userId, Map.of("bookingId", bookingId));
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, Integer from, Integer size) {
        return get("/bookings?state={state}&from={from}&size={size}",
                userId,
                Map.of(
                        "state", state,
                        "from", from == null ? 0 : from,
                        "size", size == null ? 20 : size
                )
        );
    }

    public ResponseEntity<Object> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        return get("/bookings/owner?state={state}&from={from}&size={size}",
                ownerId,
                Map.of(
                        "state", state,
                        "from", from == null ? 0 : from,
                        "size", size == null ? 20 : size
                )
        );
    }
}
