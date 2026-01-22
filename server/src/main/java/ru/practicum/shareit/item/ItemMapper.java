package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toDto(Item item) {
        if (item == null) return null;

        Long requestId = (item.getRequest() == null) ? null : item.getRequest().getId();

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static Item fromDto(ItemDto dto) {
        if (dto == null) return null;

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item,
                                                            Booking last,
                                                            Booking next,
                                                            List<CommentDto> comments) {
        if (item == null) return null;

        Long requestId = (item.getRequest() == null) ? null : item.getRequest().getId();

        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .lastBooking(toShortBooking(last))
                .nextBooking(toShortBooking(next))
                .comments(comments)
                .build();
    }

    public static ItemWithBookingsDto.BookingShortDto toShortBooking(Booking booking) {
        if (booking == null) {
            return null;
        }
        return ItemWithBookingsDto.BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static CommentDto toCommentDto(Comment c) {
        if (c == null) return null;

        return CommentDto.builder()
                .id(c.getId())
                .text(c.getText())
                .authorName(c.getAuthor().getName())
                .created(c.getCreated())
                .build();
    }
}
