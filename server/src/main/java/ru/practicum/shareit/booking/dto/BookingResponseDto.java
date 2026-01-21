package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;

    private ItemShortDto item;
    private UserShortDto booker;

    @Data
    @Builder
    public static class ItemShortDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class UserShortDto {
        private Long id;
    }
}
