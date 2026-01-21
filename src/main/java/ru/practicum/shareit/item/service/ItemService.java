package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item findById(Long id);

    List<Item> findAllByOwnerId(Long userId);

    Item create(Item item, Long ownerId);

    Item update(Long itemId, Long userId, Item item);

    void deleteById(Long itemId, Long userId);

    List<Item> search(String text);

    List<ItemWithBookingsDto> getOwnerItems(Long ownerId);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);

    ItemWithBookingsDto getItemByIdWithBookings(Long userId, Long itemId);
}
