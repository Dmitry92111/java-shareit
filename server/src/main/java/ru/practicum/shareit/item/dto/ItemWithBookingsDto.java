package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    private List<CommentDto> comments;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    @Data
    @Builder
    public static class BookingShortDto {
        private Long id;
        private Long bookerId;
    }
}
